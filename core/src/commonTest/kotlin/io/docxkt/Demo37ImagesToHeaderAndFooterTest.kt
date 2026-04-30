package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo37ImagesToHeaderAndFooterTest :
    DocxFixtureTest("demo-37-images-to-header-and-footer") {

    override fun build(): Document {
        val image1 = loadFixtureBytes("demo-37-images-to-header-and-footer", "word/media/image1.jpg")
        val pizza = loadFixtureBytes("demo-37-images-to-header-and-footer", "word/media/image2.gif")
        return document {
            header {
                paragraph {
                    image(
                        bytes = image1,
                        widthEmus = 952_500,
                        heightEmus = 952_500,
                        format = ImageFormat.JPEG,
                    )
                }
                paragraph {
                    image(
                        bytes = pizza,
                        widthEmus = 952_500,
                        heightEmus = 952_500,
                        format = ImageFormat.GIF,
                    )
                }
                paragraph {
                    image(
                        bytes = image1,
                        widthEmus = 952_500,
                        heightEmus = 952_500,
                        format = ImageFormat.JPEG,
                    )
                }
            }
            paragraph { text("Hello World") }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/_rels/header1.xml.rels",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.jpg",
        "word/media/image2.gif",
    )
}
