// No upstream analogue — Android-specific convenience layer over :core.
package io.docxkt.android

/**
 * Standard OOXML Wordprocessing document MIME type.
 *
 * Pinned here once so extension functions and public helpers use the same
 * string, and so callers that need to set `type = ...` on an Intent or a
 * `ContentValues` row can reference it instead of copy-pasting.
 */
public const val DOCX_MIME_TYPE: String =
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
