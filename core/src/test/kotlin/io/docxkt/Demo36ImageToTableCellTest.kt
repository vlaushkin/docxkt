package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.dsl.TableScope
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.FixtureLoader

internal class Demo36ImageToTableCellTest : DocxFixtureTest("demo-36-image-to-table-cell") {

    override fun build(): Document {
        val img = FixtureLoader.loadBinaryPart(
            fixtureName = "demo-36-image-to-table-cell",
            partPath = "word/media/image1.jpg",
        )
        val tableBlock: TableScope.() -> Unit = {
            row {
                cell {}; cell {}; cell {}; cell {}
            }
            row {
                cell {}
                cell {
                    paragraph {
                        image(
                            bytes = img,
                            widthEmus = 952_500,
                            heightEmus = 952_500,
                            format = ImageFormat.JPEG,
                        )
                    }
                }
            }
            row {
                cell {}; cell {}
            }
            row {
                cell {}; cell {}
            }
        }
        return document {
            header { table(tableBlock) }
            table(tableBlock)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/_rels/header1.xml.rels",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.jpg",
    )
}
