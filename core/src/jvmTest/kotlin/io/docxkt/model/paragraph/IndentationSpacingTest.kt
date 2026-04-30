// No upstream analogue — Indentation + Spacing data class semantics.
package io.docxkt.model.paragraph

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class IndentationDataTest {

    @Test fun `default is all-null`() {
        val i = Indentation()
        assertEquals(null, i.start)
        assertEquals(null, i.end)
        assertEquals(null, i.left)
        assertEquals(null, i.right)
        assertEquals(null, i.hanging)
        assertEquals(null, i.firstLine)
    }

    @Test fun `isEmpty true for all-null`() {
        assertTrue(Indentation().isEmpty())
    }

    @Test fun `isEmpty false when start set`() {
        assertFalse(Indentation(start = 100).isEmpty())
    }

    @Test fun `isEmpty false when firstLine set`() {
        assertFalse(Indentation(firstLine = 360).isEmpty())
    }

    @Test fun `isEmpty false when zero passed (zero != null)`() {
        // Pass-through: 0 is a real value, not "unset".
        assertFalse(Indentation(start = 0).isEmpty())
    }

    @Test fun `data class equality`() {
        assertEquals(Indentation(start = 100), Indentation(start = 100))
        assertEquals(Indentation(left = 720, right = 720), Indentation(left = 720, right = 720))
    }

    @Test fun `negative twips allowed in data class`() {
        val i = Indentation(left = -100)
        assertEquals(-100, i.left)
    }
}

internal class SpacingDataTest {

    @Test fun `default is all-null`() {
        val s = Spacing()
        assertEquals(null, s.before)
        assertEquals(null, s.after)
        assertEquals(null, s.line)
        assertEquals(null, s.lineRule)
        assertEquals(null, s.beforeAutoSpacing)
        assertEquals(null, s.afterAutoSpacing)
    }

    @Test fun `isEmpty true for all-null`() {
        assertTrue(Spacing().isEmpty())
    }

    @Test fun `isEmpty false when before set`() {
        assertFalse(Spacing(before = 240).isEmpty())
    }

    @Test fun `isEmpty false when lineRule set`() {
        assertFalse(Spacing(lineRule = LineRule.AUTO).isEmpty())
    }

    @Test fun `isEmpty false when boolean autoSpacing set`() {
        assertFalse(Spacing(beforeAutoSpacing = true).isEmpty())
        assertFalse(Spacing(afterAutoSpacing = false).isEmpty())
    }

    @Test fun `data class equality`() {
        assertEquals(
            Spacing(before = 100, after = 200, line = 300),
            Spacing(before = 100, after = 200, line = 300),
        )
    }
}

internal class LineRuleTest {

    @Test fun `AUTO wire`() = assertEquals("auto", LineRule.AUTO.wire)
    @Test fun `EXACT wire`() = assertEquals("exact", LineRule.EXACT.wire)
    @Test fun `EXACTLY wire`() = assertEquals("exactly", LineRule.EXACTLY.wire)
    @Test fun `AT_LEAST wire camelCase`() = assertEquals("atLeast", LineRule.AT_LEAST.wire)
    @Test fun `enum has 4 values - upstream keeps EXACT and EXACTLY both`() =
        assertEquals(4, LineRule.values().size)
}
