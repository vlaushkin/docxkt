package io.docxkt.testing

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import platform.posix.getenv
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString

/**
 * Apple-Native `actual` for the multiplatform fixture loader. Reads
 * from the absolute filesystem path supplied by `DOCXKT_FIXTURES_ROOT`
 * — wired by `core/build.gradle.kts` per Native test task. The
 * directory is the project's `src/jvmTest/resources/fixtures/`; we
 * read directly off the host disk rather than copying into the test
 * bundle so the same on-disk fixtures the JVM regression net hits are
 * exercised on Apple targets without a duplication step.
 *
 * iOS Simulator processes inherit the host's macOS filesystem
 * permissions for absolute paths the user can read, which is enough
 * for our test layout.
 */
@OptIn(ExperimentalForeignApi::class)
internal actual fun loadFixtureBytes(fixtureName: String, partPath: String): ByteArray {
    // env var first (cheap to override at runtime); generated constant
    // second (works inside iOS Simulator where simctl strips env vars).
    val root = getenv("DOCXKT_FIXTURES_ROOT")?.toKString() ?: FIXTURES_ROOT_BAKED
    val path = Path(root, fixtureName, partPath)
    return SystemFileSystem.source(path).buffered().use { it.readByteArray() }
}
