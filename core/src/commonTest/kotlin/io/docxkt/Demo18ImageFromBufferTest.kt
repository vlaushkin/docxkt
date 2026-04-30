package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo18ImageFromBufferTest : DocxFixtureTest("demo-18-image-from-buffer") {

    override fun build(): Document {
        val png = loadFixtureBytes("demo-18-image-from-buffer", "buffer.png")
        val emusPerPixel = 9525
        return document {
            paragraph {
                text("") {
                    image(
                        bytes = png,
                        widthEmus = 100 * emusPerPixel,
                        heightEmus = 100 * emusPerPixel,
                        format = ImageFormat.PNG,
                    )
                }
            }
        }
    }
}
