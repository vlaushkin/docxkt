// No upstream analogue — sample-android Activity exercises :android extensions.
package io.docxkt.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import io.docxkt.android.buildShareIntent
import io.docxkt.android.saveToDownloads
import io.docxkt.api.document

/**
 * Minimal sample of the :android wrapper. Two buttons:
 *
 * 1. **Save to Downloads** — calls [io.docxkt.android.saveToDownloads]
 *    which uses scoped storage on Android 10+ and the legacy public
 *    Downloads path on older versions.
 * 2. **Share** — calls [io.docxkt.android.buildShareIntent] to produce
 *    an `ACTION_SEND` intent carrying a FileProvider-backed URI, then
 *    launches a chooser.
 *
 * No coroutines, no binding generation — this is a reference, not a
 * production template. All I/O happens on the main thread for brevity;
 * production callers should dispatch to a background thread.
 */
internal class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(32), dp(16), dp(16))
        }

        root.addView(TextView(this).apply {
            setText(R.string.intro)
            textSize = 16f
            setPadding(0, 0, 0, dp(16))
        })

        root.addView(Button(this).apply {
            setText(R.string.action_save_downloads)
            setOnClickListener { onSaveToDownloads() }
        })

        root.addView(Button(this).apply {
            setText(R.string.action_share)
            setOnClickListener { onShare() }
        })

        setContentView(root)
    }

    private fun onSaveToDownloads() {
        runAndToast {
            val uri = buildSampleDocument().saveToDownloads(
                context = this@MainActivity,
                displayName = "docxkt-sample.docx",
            )
            Toast.makeText(
                this@MainActivity,
                getString(R.string.saved_toast, uri.toString()),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun onShare() {
        runAndToast {
            val intent = buildSampleDocument().buildShareIntent(
                context = this@MainActivity,
                filename = "docxkt-sample.docx",
                authority = "$packageName.fileprovider",
                subject = "docxkt sample",
            )
            startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)))
        }
    }

    private inline fun runAndToast(block: () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            Toast.makeText(
                this,
                getString(R.string.error_toast, t.message ?: t.javaClass.simpleName),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun buildSampleDocument() = document {
        paragraph {
            text("docxkt sample") {
                bold = true
                size = 32
            }
        }
        paragraph {
            text("Generated on ${java.time.LocalDateTime.now()}")
        }
        paragraph {
            text("• Plain text")
        }
        paragraph {
            text("• Bold") { bold = true }
            text(", ")
            text("italic") { italics = true }
            text(", ")
            text("underlined") {
                underline(io.docxkt.model.paragraph.run.UnderlineType.SINGLE)
            }
        }
        paragraph {
            text("• Emoji / CJK round-trip: 😀 𠮷 テスト مرحبا")
        }
    }

    private fun dp(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
