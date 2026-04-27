// No upstream analogue — bookmark wire shape and attribute order.
package io.docxkt.model.bookmark

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class BookmarkStartTest {

    private fun render(b: BookmarkStart): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `emits w bookmarkStart with name and id`() {
        val xml = render(BookmarkStart(id = 1, name = "section1"))
        assertEquals("""<w:bookmarkStart w:name="section1" w:id="1"/>""", xml)
    }

    @Test fun `attribute order is name then id (matches upstream constructor push)`() {
        val xml = render(BookmarkStart(id = 7, name = "x"))
        val n = xml.indexOf("w:name=")
        val i = xml.indexOf("w:id=")
        assertTrue(n in 0 until i, "expected name before id; got $xml")
    }

    @Test fun `name with XML-significant chars is escaped`() {
        val xml = render(BookmarkStart(id = 1, name = "a < b"))
        assertTrue("""w:name="a &lt; b"""" in xml)
    }

    @Test fun `large id propagates`() {
        val xml = render(BookmarkStart(id = 999_999, name = "x"))
        assertTrue("""w:id="999999"""" in xml)
    }

    @Test fun `id zero is allowed`() {
        val xml = render(BookmarkStart(id = 0, name = "x"))
        assertTrue("""w:id="0"""" in xml)
    }
}

internal class BookmarkEndTest {

    private fun render(b: BookmarkEnd): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `emits w bookmarkEnd with id only`() {
        val xml = render(BookmarkEnd(id = 1))
        assertEquals("""<w:bookmarkEnd w:id="1"/>""", xml)
    }

    @Test fun `id zero allowed`() {
        val xml = render(BookmarkEnd(id = 0))
        assertTrue("""w:id="0"""" in xml)
    }

    @Test fun `large id propagates`() {
        val xml = render(BookmarkEnd(id = 12345))
        assertTrue("""w:id="12345"""" in xml)
    }
}
