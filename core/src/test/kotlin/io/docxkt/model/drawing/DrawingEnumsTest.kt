// No upstream analogue — coverage of drawing-related enums and
// ImageFormat utility fields.
package io.docxkt.model.drawing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class HorizontalRelativeFromTest {
    @Test fun `CHARACTER wire`() = assertEquals("character", HorizontalRelativeFrom.CHARACTER.wire)
    @Test fun `COLUMN wire`() = assertEquals("column", HorizontalRelativeFrom.COLUMN.wire)
    @Test fun `INSIDE_MARGIN wire`() = assertEquals("insideMargin", HorizontalRelativeFrom.INSIDE_MARGIN.wire)
    @Test fun `LEFT_MARGIN wire`() = assertEquals("leftMargin", HorizontalRelativeFrom.LEFT_MARGIN.wire)
    @Test fun `MARGIN wire`() = assertEquals("margin", HorizontalRelativeFrom.MARGIN.wire)
    @Test fun `OUTSIDE_MARGIN wire`() = assertEquals("outsideMargin", HorizontalRelativeFrom.OUTSIDE_MARGIN.wire)
    @Test fun `PAGE wire`() = assertEquals("page", HorizontalRelativeFrom.PAGE.wire)
    @Test fun `RIGHT_MARGIN wire`() = assertEquals("rightMargin", HorizontalRelativeFrom.RIGHT_MARGIN.wire)
    @Test fun `enum has 8 values`() = assertEquals(8, HorizontalRelativeFrom.values().size)
}

internal class VerticalRelativeFromTest {
    @Test fun `BOTTOM_MARGIN wire`() = assertEquals("bottomMargin", VerticalRelativeFrom.BOTTOM_MARGIN.wire)
    @Test fun `INSIDE_MARGIN wire`() = assertEquals("insideMargin", VerticalRelativeFrom.INSIDE_MARGIN.wire)
    @Test fun `LINE wire`() = assertEquals("line", VerticalRelativeFrom.LINE.wire)
    @Test fun `MARGIN wire`() = assertEquals("margin", VerticalRelativeFrom.MARGIN.wire)
    @Test fun `OUTSIDE_MARGIN wire`() = assertEquals("outsideMargin", VerticalRelativeFrom.OUTSIDE_MARGIN.wire)
    @Test fun `PAGE wire`() = assertEquals("page", VerticalRelativeFrom.PAGE.wire)
    @Test fun `PARAGRAPH wire`() = assertEquals("paragraph", VerticalRelativeFrom.PARAGRAPH.wire)
    @Test fun `TOP_MARGIN wire`() = assertEquals("topMargin", VerticalRelativeFrom.TOP_MARGIN.wire)
    @Test fun `enum has 8 values`() = assertEquals(8, VerticalRelativeFrom.values().size)
}

internal class HorizontalAlignTest {
    @Test fun `CENTER wire`() = assertEquals("center", HorizontalAlign.CENTER.wire)
    @Test fun `INSIDE wire`() = assertEquals("inside", HorizontalAlign.INSIDE.wire)
    @Test fun `LEFT wire`() = assertEquals("left", HorizontalAlign.LEFT.wire)
    @Test fun `OUTSIDE wire`() = assertEquals("outside", HorizontalAlign.OUTSIDE.wire)
    @Test fun `RIGHT wire`() = assertEquals("right", HorizontalAlign.RIGHT.wire)
}

internal class VerticalAlignDrawingTest {
    @Test fun `BOTTOM wire`() = assertEquals("bottom", VerticalAlign.BOTTOM.wire)
    @Test fun `CENTER wire`() = assertEquals("center", VerticalAlign.CENTER.wire)
    @Test fun `INSIDE wire`() = assertEquals("inside", VerticalAlign.INSIDE.wire)
    @Test fun `OUTSIDE wire`() = assertEquals("outside", VerticalAlign.OUTSIDE.wire)
    @Test fun `TOP wire`() = assertEquals("top", VerticalAlign.TOP.wire)
}

internal class ImageFormatTest {

    @Test fun `PNG extension is png`() {
        assertEquals("png", ImageFormat.PNG.extension)
    }

    @Test fun `PNG mimeType is image-png`() {
        assertEquals("image/png", ImageFormat.PNG.mimeType)
    }

    @Test fun `JPEG extension is jpg`() {
        // jpg, not jpeg — matches upstream's image{N}.jpg naming.
        assertEquals("jpg", ImageFormat.JPEG.extension)
    }

    @Test fun `JPEG mimeType is image-jpeg`() {
        assertEquals("image/jpeg", ImageFormat.JPEG.mimeType)
    }

    @Test fun `GIF extension is gif`() {
        assertEquals("gif", ImageFormat.GIF.extension)
    }

    @Test fun `BMP extension is bmp`() {
        assertEquals("bmp", ImageFormat.BMP.extension)
    }

    @Test fun `enum covers raster formats used by upstream demos`() {
        // PNG, JPEG, GIF, BMP.
        assertEquals(4, ImageFormat.values().size)
    }
}
