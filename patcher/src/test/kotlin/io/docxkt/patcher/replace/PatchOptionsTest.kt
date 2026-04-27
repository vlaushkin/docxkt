// No upstream analogue — PatchOptions regex construction edge cases.
package io.docxkt.patcher.replace

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PatchOptionsTest {

    @Test fun `defaults match upstream patchDocument defaults`() {
        val o = PatchOptions()
        assertTrue(o.keepOriginalStyles)
        assertEquals("{{", o.placeholderStart)
        assertEquals("}}", o.placeholderEnd)
        assertTrue(o.recursive)
    }

    @Test fun `default regex matches simple key`() {
        val regex = PatchOptions().buildMarkerRegex()
        val m = regex.find("Hello {{name}}!")
        assertNotNull(m)
        assertEquals("name", m.groupValues[1])
    }

    @Test fun `default regex captures any non-greedy inner content`() {
        val regex = PatchOptions().buildMarkerRegex()
        // (.+?) means at least one char between the delimiters.
        val m = regex.find("a {{x}} b {{y}}")
        assertNotNull(m)
        assertEquals("x", m.groupValues[1])
    }

    @Test fun `custom delimiters with regex-special chars escaped`() {
        // $$ is regex-meaningful; Regex.escape should make it literal.
        val o = PatchOptions(placeholderStart = "$$", placeholderEnd = "$$")
        val regex = o.buildMarkerRegex()
        val m = regex.find("Hello \$\$name\$\$!")
        assertNotNull(m)
        assertEquals("name", m.groupValues[1])
    }

    @Test fun `custom delimiters with brackets escaped`() {
        // [[...]] - [ and ] are regex-character-class chars.
        val o = PatchOptions(placeholderStart = "[[", placeholderEnd = "]]")
        val regex = o.buildMarkerRegex()
        val m = regex.find("Hello [[name]]!")
        assertNotNull(m)
        assertEquals("name", m.groupValues[1])
    }

    @Test fun `custom delimiters with parens escaped`() {
        val o = PatchOptions(placeholderStart = "((", placeholderEnd = "))")
        val regex = o.buildMarkerRegex()
        val m = regex.find("Hello ((name))!")
        assertNotNull(m)
        assertEquals("name", m.groupValues[1])
    }

    @Test fun `custom delimiters with mixed lengths`() {
        val o = PatchOptions(placeholderStart = "<%", placeholderEnd = "%>")
        val regex = o.buildMarkerRegex()
        val m = regex.find("Hello <%name%>!")
        assertNotNull(m)
        assertEquals("name", m.groupValues[1])
    }

    @Test fun `delimiter does not match unrelated text`() {
        val o = PatchOptions()
        val regex = o.buildMarkerRegex()
        // No delimiters present.
        assertNull(regex.find("plain text without markers"))
    }

    @Test fun `non-greedy match prefers shortest body`() {
        val o = PatchOptions()
        val regex = o.buildMarkerRegex()
        val matches = regex.findAll("{{a}} {{b}} {{c}}").toList()
        assertEquals(3, matches.size)
        assertEquals(listOf("a", "b", "c"), matches.map { it.groupValues[1] })
    }

    @Test fun `keepOriginalStyles propagates`() {
        assertEquals(false, PatchOptions(keepOriginalStyles = false).keepOriginalStyles)
    }

    @Test fun `recursive propagates`() {
        assertEquals(false, PatchOptions(recursive = false).recursive)
    }
}
