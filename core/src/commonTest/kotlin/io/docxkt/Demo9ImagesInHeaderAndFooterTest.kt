package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo9ImagesInHeaderAndFooterTest :
    DocxFixtureTest("demo-9-images-in-header-and-footer") {

    override fun build(): Document {
        val pizzaBytes = loadFixtureBytes("demo-9-images-in-header-and-footer", "word/media/image1.gif")
        return document {
            header {
                paragraph {
                    image(
                        bytes = pizzaBytes,
                        widthEmus = 952_500,
                        heightEmus = 952_500,
                        format = ImageFormat.GIF,
                    )
                }
            }
            footer {
                paragraph {
                    image(
                        bytes = pizzaBytes,
                        widthEmus = 952_500,
                        heightEmus = 952_500,
                        format = ImageFormat.GIF,
                    )
                }
            }
            paragraph { text("Hello World") }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/footer1.xml",
        "word/_rels/header1.xml.rels",
        "word/_rels/footer1.xml.rels",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.gif",
    )
}
