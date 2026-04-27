// No upstream analogue — Android-specific convenience layer over :core.
package io.docxkt.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import io.docxkt.api.Document
import java.io.File

/**
 * Write this [Document] to a temporary file under the app's cache
 * directory and return a `content://` [Uri] backed by a [FileProvider].
 *
 * The caller's app must declare a `FileProvider` in its
 * `AndroidManifest.xml` and a `file_paths` XML resource exposing the
 * subdirectory used here (`cache-path` with the same [subdirectory]
 * name). Example manifest entry:
 *
 * ```xml
 * <provider
 *     android:name="androidx.core.content.FileProvider"
 *     android:authorities="${applicationId}.fileprovider"
 *     android:exported="false"
 *     android:grantUriPermissions="true">
 *   <meta-data
 *       android:name="android.support.FILE_PROVIDER_PATHS"
 *       android:resource="@xml/file_paths" />
 * </provider>
 * ```
 *
 * And the paths XML:
 *
 * ```xml
 * <paths>
 *   <cache-path name="docx" path="docx/" />
 * </paths>
 * ```
 *
 * Overwrites any existing file of the same name under the cache
 * subdirectory.
 *
 * @param filename user-visible filename inside the cache (e.g. `report.docx`).
 * @param authority the `android:authorities` value of the provider.
 * @param subdirectory subdirectory under [Context.getCacheDir]; defaults
 *        to `"docx"`. Must match the `<cache-path>` declared in
 *        `file_paths.xml`.
 */
public fun Document.asFileProviderUri(
    context: Context,
    filename: String,
    authority: String,
    subdirectory: String = "docx",
): Uri {
    val cacheDir = File(context.cacheDir, subdirectory)
    cacheDir.mkdirs()
    val file = File(cacheDir, filename)
    file.outputStream().use { writeTo(it) }
    return FileProvider.getUriForFile(context, authority, file)
}

/**
 * Build an [Intent.ACTION_SEND] intent carrying this document as an
 * attachment. The URI is granted read permission to whatever activity
 * the user picks from the share sheet via
 * [Intent.FLAG_GRANT_READ_URI_PERMISSION].
 *
 * Usage:
 * ```kotlin
 * val intent = doc.buildShareIntent(context, "report.docx", "${packageName}.fileprovider")
 * startActivity(Intent.createChooser(intent, "Share report"))
 * ```
 *
 * See [asFileProviderUri] for the manifest setup required in the
 * caller's app.
 */
public fun Document.buildShareIntent(
    context: Context,
    filename: String,
    authority: String,
    subject: String? = null,
): Intent {
    val uri = asFileProviderUri(context, filename, authority)
    return Intent(Intent.ACTION_SEND).apply {
        type = DOCX_MIME_TYPE
        putExtra(Intent.EXTRA_STREAM, uri)
        if (subject != null) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
