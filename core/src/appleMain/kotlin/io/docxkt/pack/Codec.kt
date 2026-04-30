// No upstream analogue. Apple-side actuals for the codec expects
// defined in commonMain/io/docxkt/pack/Codec.kt. Binds the system libz
// through Kotlin/Native's bundled `platform.zlib` interop.
//
// `windowBits = -15` is the libz flag for "raw DEFLATE" (RFC 1951, no
// zlib/gzip wrapper, no Adler-32 trailer) — required to match JDK's
// `Deflater(nowrap = true)` byte-for-byte. Apple Compression
// framework's COMPRESSION_ZLIB writes RFC 1950 only and is NOT used.
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package io.docxkt.pack

import platform.zlib.Z_BUF_ERROR
import platform.zlib.Z_DEFAULT_COMPRESSION
import platform.zlib.Z_DEFAULT_STRATEGY
import platform.zlib.Z_DEFLATED
import platform.zlib.Z_FINISH
import platform.zlib.Z_NO_FLUSH
import platform.zlib.Z_OK
import platform.zlib.Z_STREAM_END
import platform.zlib.ZLIB_VERSION
import platform.zlib.crc32 as zlibCrc32
import platform.zlib.deflate as zlibDeflate
import platform.zlib.deflateEnd
import platform.zlib.deflateInit2_
import platform.zlib.inflate as zlibInflate
import platform.zlib.inflateEnd
import platform.zlib.inflateInit2_
import platform.zlib.z_stream
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pin
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString

/** libz default memory level (matches JDK / ZipOutputStream defaults). */
private const val Z_DEFAULT_MEM_LEVEL: Int = 8

/** "Raw deflate" window-bits flag — negative = no zlib/gzip wrapper. */
private const val RAW_WINDOW_BITS: Int = -15

internal actual class Deflater actual constructor() {
    actual fun deflate(input: ByteArray): ByteArray = memScoped {
        val zs = alloc<z_stream>()
        zs.zalloc = null
        zs.zfree = null
        zs.opaque = null
        val initRc = deflateInit2_(
            zs.ptr,
            Z_DEFAULT_COMPRESSION,
            Z_DEFLATED,
            RAW_WINDOW_BITS,
            Z_DEFAULT_MEM_LEVEL,
            Z_DEFAULT_STRATEGY,
            ZLIB_VERSION,
            sizeOf<z_stream>().convert(),
        )
        check(initRc == Z_OK) { "zlib deflateInit2_ failed: rc=$initRc" }

        try {
            val pinned = input.pin()
            try {
                if (input.isNotEmpty()) {
                    zs.next_in = pinned.addressOf(0).reinterpret<UByteVar>()
                }
                zs.avail_in = input.size.convert()

                val chunks = ArrayList<ByteArray>()
                // Sized to typically allow the whole stream in one
                // pump for ZIP-entry-sized inputs (a few KB to MB).
                val outBuf = ByteArray(maxOf(64, input.size + 64))
                val outPin = outBuf.pin()
                try {
                    while (true) {
                        zs.next_out = outPin.addressOf(0).reinterpret<UByteVar>()
                        zs.avail_out = outBuf.size.convert()
                        val rc = zlibDeflate(zs.ptr, Z_FINISH)
                        val produced = outBuf.size - zs.avail_out.toInt()
                        if (produced > 0) chunks += outBuf.copyOf(produced)
                        if (rc == Z_STREAM_END) break
                        check(rc == Z_OK || rc == Z_BUF_ERROR) {
                            "zlib deflate failed: rc=$rc msg=${zs.msg?.toKString()}"
                        }
                    }
                } finally {
                    outPin.unpin()
                }

                val total = chunks.sumOf { it.size }
                val merged = ByteArray(total)
                var off = 0
                for (c in chunks) {
                    c.copyInto(merged, off)
                    off += c.size
                }
                merged
            } finally {
                pinned.unpin()
            }
        } finally {
            deflateEnd(zs.ptr)
        }
    }
}

internal actual class Inflater actual constructor() {
    actual fun inflate(input: ByteArray): ByteArray = memScoped {
        val zs = alloc<z_stream>()
        zs.zalloc = null
        zs.zfree = null
        zs.opaque = null
        val initRc = inflateInit2_(
            zs.ptr,
            RAW_WINDOW_BITS,
            ZLIB_VERSION,
            sizeOf<z_stream>().convert(),
        )
        check(initRc == Z_OK) { "zlib inflateInit2_ failed: rc=$initRc" }

        try {
            val pinned = input.pin()
            try {
                if (input.isNotEmpty()) {
                    zs.next_in = pinned.addressOf(0).reinterpret<UByteVar>()
                }
                zs.avail_in = input.size.convert()

                val chunks = ArrayList<ByteArray>()
                val outBuf = ByteArray(maxOf(1024, input.size * 2))
                val outPin = outBuf.pin()
                try {
                    while (true) {
                        zs.next_out = outPin.addressOf(0).reinterpret<UByteVar>()
                        zs.avail_out = outBuf.size.convert()
                        val rc = zlibInflate(zs.ptr, Z_NO_FLUSH)
                        val produced = outBuf.size - zs.avail_out.toInt()
                        if (produced > 0) chunks += outBuf.copyOf(produced)
                        if (rc == Z_STREAM_END) break
                        check(rc == Z_OK) {
                            "zlib inflate failed: rc=$rc msg=${zs.msg?.toKString()}"
                        }
                        // Z_OK with no progress AND no remaining input
                        // means the stream is truncated.
                        if (produced == 0 && zs.avail_in.toInt() == 0) {
                            error("zlib inflate: truncated stream")
                        }
                    }
                } finally {
                    outPin.unpin()
                }

                val total = chunks.sumOf { it.size }
                val merged = ByteArray(total)
                var off = 0
                for (c in chunks) {
                    c.copyInto(merged, off)
                    off += c.size
                }
                merged
            } finally {
                pinned.unpin()
            }
        } finally {
            inflateEnd(zs.ptr)
        }
    }
}

internal actual fun crc32(bytes: ByteArray): Long {
    if (bytes.isEmpty()) return 0L
    val pinned = bytes.pin()
    try {
        // libz `crc32(0, buf, len)` is CRC-32/ISO-HDLC (init=0,
        // poly=0xEDB88320, refin=true, refout=true, xorout=0xFFFFFFFF).
        // Identical to java.util.zip.CRC32.
        return zlibCrc32(
            0u.convert(),
            pinned.addressOf(0).reinterpret<UByteVar>(),
            bytes.size.convert(),
        ).toLong()
    } finally {
        pinned.unpin()
    }
}
