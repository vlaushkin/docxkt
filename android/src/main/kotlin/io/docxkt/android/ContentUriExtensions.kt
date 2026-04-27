// No upstream analogue — Android-specific convenience layer over :core.
package io.docxkt.android

import android.content.Context
import android.net.Uri
import io.docxkt.api.Document

/**
 * Write this [Document] as a `.docx` ZIP to the given content [uri] via the
 * [Context]'s [android.content.ContentResolver].
 *
 * Typical call sites:
 *
 * - a `content://` URI obtained from the Storage Access Framework
 *   (`Intent.ACTION_CREATE_DOCUMENT`);
 * - a URI produced by `MediaStore.Downloads.EXTERNAL_CONTENT_URI` on
 *   Android 10+ scoped-storage flows;
 * - a `FileProvider`-backed URI used for share intents, where a
 *   background thread writes the file before the intent is launched.
 *
 * The caller must have write permission on [uri]. This function does not
 * persist the granted permission (`takePersistableUriPermission`) — that's
 * the caller's responsibility where applicable.
 *
 * The underlying [java.io.OutputStream] is always closed, including on
 * failure (buffered or not — `use { }` handles both).
 *
 * @throws IllegalArgumentException if [Context.getContentResolver] returns
 *         a null [java.io.OutputStream] for the given [uri] (usually means
 *         the provider refuses to serve it, or the URI is malformed).
 */
public fun Document.writeTo(context: Context, uri: Uri) {
    val resolver = context.contentResolver
    val stream = resolver.openOutputStream(uri)
        ?: throw IllegalArgumentException(
            "ContentResolver.openOutputStream returned null for: $uri"
        )
    stream.use { writeTo(it) }
}
