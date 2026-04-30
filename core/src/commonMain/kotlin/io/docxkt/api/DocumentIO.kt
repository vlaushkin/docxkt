// Port of: src/export/packer/ (writeTo / toBuffer equivalents).
// Upstream uses Node Buffer / Blob; we use kotlinx.io.Sink as the
// KMP-portable IO target.
package io.docxkt.api

import io.docxkt.pack.DocxPackager
import io.docxkt.pack.pack
import io.docxkt.pack.toByteArray
import kotlinx.io.Sink

/**
 * Write the document as a `.docx` ZIP into [sink]. Does not close
 * [sink] — the caller owns its lifecycle. Calls [Sink.flush] on
 * return so any buffered bytes have been pushed to the underlying
 * raw sink before this function returns.
 */
public fun Document.writeTo(sink: Sink) {
    DocxPackager.pack(assembleEntries(), sink)
}

/** Serialize the document to a `.docx` ZIP as a byte array. */
public fun Document.toByteArray(): ByteArray = DocxPackager.toByteArray(assembleEntries())
