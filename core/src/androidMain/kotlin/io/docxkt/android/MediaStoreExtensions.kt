// No upstream analogue — Android-specific convenience layer over :core.
package io.docxkt.android

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import io.docxkt.api.Document
import io.docxkt.api.writeTo
import java.io.File

/**
 * Save this [Document] to the public Downloads folder.
 *
 * - **Android 10 (API 29)+**: uses scoped storage via
 *   [MediaStore.Downloads]. No permissions required.
 * - **Android 9 and below**: writes directly to
 *   [Environment.getExternalStoragePublicDirectory] under
 *   [Environment.DIRECTORY_DOWNLOADS]. Caller must have
 *   `WRITE_EXTERNAL_STORAGE`.
 *
 * [displayName] is the user-visible filename. A `.docx` suffix is added
 * if not already present. [subdirectory] is an optional relative path
 * under Downloads (created if missing).
 *
 * Returns the [Uri] of the saved file, suitable for further actions
 * like sharing, opening in another app, or adding to a shortlist.
 *
 * @throws IllegalStateException if the system refuses to insert the
 *         entry (full storage, policy block, etc.).
 */
public fun Document.saveToDownloads(
    context: Context,
    displayName: String,
    subdirectory: String? = null,
): Uri {
    val filename = if (displayName.endsWith(".docx", ignoreCase = true)) {
        displayName
    } else {
        "$displayName.docx"
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveViaMediaStore(context, filename, subdirectory)
    } else {
        saveToDownloadsLegacy(filename, subdirectory)
    }
}

@Suppress("DEPRECATION")
private fun Document.saveToDownloadsLegacy(
    filename: String,
    subdirectory: String?,
): Uri {
    val downloads = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS,
    )
    val dir = if (subdirectory != null) File(downloads, subdirectory) else downloads
    dir.mkdirs()
    val file = File(dir, filename)
    file.outputStream().use { writeTo(it) }
    return Uri.fromFile(file)
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun Document.saveViaMediaStore(
    context: Context,
    filename: String,
    subdirectory: String?,
): Uri {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, filename)
        put(MediaStore.Downloads.MIME_TYPE, DOCX_MIME_TYPE)
        val relative = if (subdirectory != null) {
            "${Environment.DIRECTORY_DOWNLOADS}/$subdirectory"
        } else {
            Environment.DIRECTORY_DOWNLOADS
        }
        put(MediaStore.Downloads.RELATIVE_PATH, relative)
        put(MediaStore.Downloads.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: throw IllegalStateException(
            "MediaStore.Downloads insert returned null for: $filename",
        )
    try {
        val stream = resolver.openOutputStream(uri)
            ?: throw IllegalStateException(
                "openOutputStream returned null for freshly inserted: $uri",
            )
        stream.use { writeTo(it) }
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    } catch (e: Throwable) {
        // Roll back the pending entry so the user doesn't see a zero-byte
        // ghost in their Downloads list.
        resolver.delete(uri, null, null)
        throw e
    }
    return uri
}
