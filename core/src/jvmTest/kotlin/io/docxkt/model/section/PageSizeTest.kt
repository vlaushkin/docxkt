package io.docxkt.model.section

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PageSizeTest {

    @Test
    fun `a4 portrait exactly matches upstream integers`() {
        val p = PageSize.a4(PageOrientation.PORTRAIT)
        assertEquals(11906, p.widthTwips)
        assertEquals(16838, p.heightTwips)
        assertEquals(PageOrientation.PORTRAIT, p.orientation)
    }

    @Test
    fun `a4 landscape swaps dimensions`() {
        val p = PageSize.a4(PageOrientation.LANDSCAPE)
        assertEquals(16838, p.widthTwips)
        assertEquals(11906, p.heightTwips)
        assertEquals(PageOrientation.LANDSCAPE, p.orientation)
    }

    @Test
    fun `a4 default is portrait`() {
        assertEquals(PageSize.a4(PageOrientation.PORTRAIT), PageSize.a4())
    }
}
