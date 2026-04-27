// No upstream analogue — Patch sealed class equality and field
// semantics tests.
package io.docxkt.patcher

import io.docxkt.api.paragraphs
import io.docxkt.api.runs
import io.docxkt.api.tableRows
import io.docxkt.model.drawing.ImageFormat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class PatchTest {

    // --- Patch.Text ----------------------------------------------------

    @Test fun `Text data class equality`() {
        assertEquals(Patch.Text("a"), Patch.Text("a"))
    }

    @Test fun `Text different value not equal`() {
        assertNotEquals(Patch.Text("a"), Patch.Text("b"))
    }

    @Test fun `Text empty value is allowed`() {
        // Empty replacement excises the marker.
        val p = Patch.Text("")
        assertEquals("", p.value)
    }

    @Test fun `Text with special chars stored verbatim`() {
        val p = Patch.Text("<a> & </b>")
        assertEquals("<a> & </b>", p.value)
    }

    // --- Patch.Image ---------------------------------------------------

    @Test fun `Image equality compares bytes by content`() {
        val a = Patch.Image(byteArrayOf(1, 2, 3), 100, 100, ImageFormat.PNG)
        val b = Patch.Image(byteArrayOf(1, 2, 3), 100, 100, ImageFormat.PNG)
        assertEquals(a, b)
    }

    @Test fun `Image equality differs when bytes differ`() {
        val a = Patch.Image(byteArrayOf(1, 2, 3), 100, 100, ImageFormat.PNG)
        val b = Patch.Image(byteArrayOf(1, 2, 4), 100, 100, ImageFormat.PNG)
        assertNotEquals(a, b)
    }

    @Test fun `Image hashCode is stable across equal-bytes instances`() {
        val a = Patch.Image(byteArrayOf(1, 2, 3), 100, 100, ImageFormat.PNG)
        val b = Patch.Image(byteArrayOf(1, 2, 3), 100, 100, ImageFormat.PNG)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test fun `Image different EMUs not equal`() {
        val a = Patch.Image(byteArrayOf(1), 100, 100, ImageFormat.PNG)
        val b = Patch.Image(byteArrayOf(1), 200, 100, ImageFormat.PNG)
        assertNotEquals(a, b)
    }

    @Test fun `Image different format not equal`() {
        val a = Patch.Image(byteArrayOf(1), 100, 100, ImageFormat.PNG)
        val b = Patch.Image(byteArrayOf(1), 100, 100, ImageFormat.JPEG)
        assertNotEquals(a, b)
    }

    // --- Patch.Paragraphs / Patch.Rows snippet wrapping ----------------

    @Test fun `Paragraphs wraps ParagraphSnippets`() {
        val snippets = paragraphs { paragraph { text("hello") } }
        val p = Patch.Paragraphs(snippets)
        assertEquals(1, p.snippets.size)
    }

    @Test fun `Rows wraps TableRowSnippets`() {
        val snippets = tableRows {
            row { cell { paragraph { text("a") } } }
            row { cell { paragraph { text("b") } } }
        }
        val p = Patch.Rows(snippets)
        assertEquals(2, p.snippets.size)
    }

    @Test fun `Patch is sealed - exhaustive when over five subtypes works`() {
        val patches: List<Patch> = listOf(
            Patch.Text("x"),
            Patch.Paragraphs(paragraphs { paragraph { text("a") } }),
            Patch.Image(byteArrayOf(1), 1, 1, ImageFormat.PNG),
            Patch.Rows(tableRows { row { cell { paragraph { text("a") } } } }),
            Patch.ParagraphInline(runs { run("a") }),
        )
        // Sealed class — when over Patch is exhaustive without `else`.
        val descriptions = patches.map { p ->
            when (p) {
                is Patch.Text -> "text"
                is Patch.Paragraphs -> "paragraphs"
                is Patch.Image -> "image"
                is Patch.Rows -> "rows"
                is Patch.ParagraphInline -> "inline"
            }
        }
        assertEquals(listOf("text", "paragraphs", "image", "rows", "inline"), descriptions)
    }
}
