// No upstream analogue. JVM/Android-only convenience wrapper around
// the commonMain `Document.writeTo(sink)` for callers that already
// hold a java.io.OutputStream.
package io.docxkt.api

import kotlinx.io.asSink
import kotlinx.io.buffered
import java.io.OutputStream

/** Write the document as a `.docx` ZIP to [out]. Does not close [out]. */
public fun Document.writeTo(out: OutputStream) {
    val sink = out.asSink().buffered()
    writeTo(sink)
    sink.flush()
}
