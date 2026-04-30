package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo12ScalingImagesTest : DocxFixtureTest("demo-12-scaling-images") {

    override fun build(): Document {
        val pizza = loadFixtureBytes("demo-12-scaling-images", "pizza.gif")
        val emusPerPixel = 9525
        return document {
            paragraph { text("Hello World") }
            for (sizePx in listOf(50, 100, 250, 400)) {
                paragraph {
                    text("") {
                        image(
                            bytes = pizza,
                            widthEmus = sizePx * emusPerPixel,
                            heightEmus = sizePx * emusPerPixel,
                            format = ImageFormat.GIF,
                        )
                    }
                }
            }
        }
    }
}
