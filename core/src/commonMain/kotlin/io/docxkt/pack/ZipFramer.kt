// No upstream analogue. dolanmiu/docx delegates ZIP framing to JSZip;
// docxkt hand-rolls a deterministic raw-DEFLATE ZIP writer in
// commonMain so JVM, Android, and Apple targets share a single byte
// path. Compressed data comes from the per-platform `Deflater` actual
// (java.util.zip on JVM/Android, libz with windowBits = -15 on Apple);
// CRC-32 from the per-platform `crc32` actual.
package io.docxkt.pack

import io.docxkt.pack.DocxPackager.Entry
import io.docxkt.pack.DocxPackager.EntryOrder
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.readByteArray
import kotlinx.io.writeIntLe
import kotlinx.io.writeShortLe

// ---- ZIP format constants. APPNOTE.TXT 6.3.x. ----

private const val LFH_SIGNATURE: Int = 0x04034b50.toInt()
private const val CDE_SIGNATURE: Int = 0x02014b50.toInt()
private const val EOCD_SIGNATURE: Int = 0x06054b50.toInt()

/** "Version needed to extract" — 2.0 supports DEFLATE. */
private const val VERSION_NEEDED: Int = 20

/**
 * "Version made by" — high byte = host system (0 = MS-DOS / FAT,
 * matching what JDK ZipOutputStream writes), low byte = ZIP-spec
 * version × 10.
 */
private const val VERSION_MADE_BY: Int = 20

/** General-purpose bit flag. 0 = no encryption, no data descriptor,
 *  CP437 filename. Our paths are ASCII so the UTF-8 flag (bit 11) is
 *  not required. */
private const val GPB_FLAG: Int = 0

/** Compression method 8 = DEFLATE. */
private const val METHOD_DEFLATE: Int = 8

/**
 * DOS time/date for 1980-01-01 00:00:00. The minimum representable
 * timestamp in DOS format and the value `DocxPackager.FIXED_TIMESTAMP_MS`
 * decodes to. Hard-coded here because we never emit any other time —
 * sidesteps JDK ZipOutputStream's well-known
 * "ZipEntry.setTime is local-tz, not UTC" gotcha that would otherwise
 * make ZIP bytes vary with the host time zone.
 */
private const val DOS_TIME: Int = 0
private const val DOS_DATE: Int = 0x0021

/**
 * Per-entry working state captured during the LFH pass and consumed
 * during the central-directory pass.
 */
private class FramedEntry(
    val nameBytes: ByteArray,
    val crc: Long,
    val compressedSize: Int,
    val uncompressedSize: Int,
    val localHeaderOffset: Int,
)

/**
 * Write [entries] as a deterministic `.docx` ZIP into [sink]. Sorts
 * entries via [EntryOrder] first; each entry is DEFLATE-compressed
 * (including empty entries — `Deflater().deflate(empty)` emits the
 * 2-byte raw stored block `[0x03, 0x00]` which JDK ZipInputStream
 * unzips to empty, matching what JDK ZipOutputStream produces for
 * empty DEFLATED entries).
 *
 * Does not close [sink].
 */
public fun DocxPackager.pack(entries: List<Entry>, sink: Sink) {
    val ordered = entries.sortedWith(EntryOrder)

    val deflater = Deflater()
    val framed = ArrayList<FramedEntry>(ordered.size)

    // Stage all bytes into a Buffer first so we can compute the central
    // directory offset (which is the buffer size after the last LFH +
    // compressed data is written). kotlinx.io.Buffer is the standard
    // in-memory Sink and gives us a cheap `transferTo(sink)` at the end.
    val staging = Buffer()
    var offset = 0

    for (entry in ordered) {
        val nameBytes = entry.path.encodeToByteArray()
        val uncompressed = entry.bytes
        val compressed = deflater.deflate(uncompressed)
        val crc = crc32(uncompressed)

        framed += FramedEntry(
            nameBytes = nameBytes,
            crc = crc,
            compressedSize = compressed.size,
            uncompressedSize = uncompressed.size,
            localHeaderOffset = offset,
        )

        // ---- Local File Header (30 + name) ----
        staging.writeIntLe(LFH_SIGNATURE)
        staging.writeShortLe(VERSION_NEEDED.toShort())
        staging.writeShortLe(GPB_FLAG.toShort())
        staging.writeShortLe(METHOD_DEFLATE.toShort())
        staging.writeShortLe(DOS_TIME.toShort())
        staging.writeShortLe(DOS_DATE.toShort())
        staging.writeIntLe(crc.toInt())
        staging.writeIntLe(compressed.size)
        staging.writeIntLe(uncompressed.size)
        staging.writeShortLe(nameBytes.size.toShort())
        staging.writeShortLe(0.toShort()) // extra length
        staging.write(nameBytes)
        staging.write(compressed)

        offset += 30 + nameBytes.size + compressed.size
    }

    val cdOffset = offset
    var cdSize = 0

    for (f in framed) {
        // ---- Central Directory Entry (46 + name) ----
        staging.writeIntLe(CDE_SIGNATURE)
        staging.writeShortLe(VERSION_MADE_BY.toShort())
        staging.writeShortLe(VERSION_NEEDED.toShort())
        staging.writeShortLe(GPB_FLAG.toShort())
        staging.writeShortLe(METHOD_DEFLATE.toShort())
        staging.writeShortLe(DOS_TIME.toShort())
        staging.writeShortLe(DOS_DATE.toShort())
        staging.writeIntLe(f.crc.toInt())
        staging.writeIntLe(f.compressedSize)
        staging.writeIntLe(f.uncompressedSize)
        staging.writeShortLe(f.nameBytes.size.toShort())
        staging.writeShortLe(0.toShort()) // extra length
        staging.writeShortLe(0.toShort()) // comment length
        staging.writeShortLe(0.toShort()) // disk start
        staging.writeShortLe(0.toShort()) // internal attrs
        staging.writeIntLe(0)             // external attrs
        staging.writeIntLe(f.localHeaderOffset)
        staging.write(f.nameBytes)

        cdSize += 46 + f.nameBytes.size
    }

    // ---- End of Central Directory (22 bytes, no comment) ----
    staging.writeIntLe(EOCD_SIGNATURE)
    staging.writeShortLe(0.toShort())                  // disk num
    staging.writeShortLe(0.toShort())                  // cd disk
    staging.writeShortLe(framed.size.toShort())        // entries on this disk
    staging.writeShortLe(framed.size.toShort())        // entries total
    staging.writeIntLe(cdSize)
    staging.writeIntLe(cdOffset)
    staging.writeShortLe(0.toShort())                  // comment length

    staging.transferTo(sink)
    sink.flush()
}

/** Convenience: pack into a freshly allocated `ByteArray`. */
public fun DocxPackager.toByteArray(entries: List<Entry>): ByteArray {
    val buffer = Buffer()
    pack(entries, buffer)
    return buffer.readByteArray()
}
