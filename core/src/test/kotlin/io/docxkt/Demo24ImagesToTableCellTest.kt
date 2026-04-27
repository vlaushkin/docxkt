package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest

internal class Demo24ImagesToTableCellTest : DocxFixtureTest("demo-24-images-to-table-cell") {

    override fun build(): Document {
        val img = javaClass
            .getResourceAsStream("/fixtures/demo-24-images-to-table-cell/image1.jpeg")!!
            .readBytes()
        val emusPerPixel = 9525
        return document {
            table {
                row { cell {}; cell {}; cell {}; cell {} }
                row {
                    cell {}
                    cell {
                        paragraph {
                            text("") {
                                image(
                                    bytes = img,
                                    widthEmus = 100 * emusPerPixel,
                                    heightEmus = 100 * emusPerPixel,
                                    format = ImageFormat.JPEG,
                                )
                            }
                        }
                    }
                    cell {}
                    cell {}
                }
                row {
                    cell {}
                    cell {}
                    cell { paragraph { text("Hello") } }
                    cell {}
                }
                row { cell {}; cell {}; cell {}; cell {} }
            }
        }
    }
}
