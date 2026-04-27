package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.table.TableWidth
import io.docxkt.model.table.VerticalMerge
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.FixtureLoader

internal class Demo43ImagesToTableCell2Test :
    DocxFixtureTest("demo-43-images-to-table-cell-2") {

    override fun build(): Document {
        val img1 = FixtureLoader.loadBinaryPart(
            fixtureName = "demo-43-images-to-table-cell-2",
            partPath = "word/media/image1.png",
        )
        val img2 = FixtureLoader.loadBinaryPart(
            fixtureName = "demo-43-images-to-table-cell-2",
            partPath = "word/media/image2.jpg",
        )
        return document {
            table {
                width(TableWidth.dxa(8640))
                // 4 columns even though row 3 lists 5 cells (the
                // 4th there is a vMerge=continue placeholder, which
                // upstream's grid logic counts as the same column as
                // row 2's vMerge=restart cell).
                columnWidths(100, 100, 100, 100)
                row {
                    cell {}
                    cell {}
                    cell {}
                    cell {
                        paragraph {
                            image(
                                bytes = img1,
                                widthEmus = 952_500,
                                heightEmus = 952_500,
                                format = ImageFormat.PNG,
                            )
                        }
                    }
                }
                row {
                    cell {}
                    cell {
                        paragraph {
                            image(
                                bytes = img2,
                                widthEmus = 952_500,
                                heightEmus = 952_500,
                                format = ImageFormat.JPEG,
                            )
                        }
                    }
                    cell {}
                    cell { verticalMerge(VerticalMerge.RESTART) }
                }
                row {
                    // Upstream auto-inserts a vMerge="continue" cell at
                    // the grid position covered by row 2's rowSpan=2 cell;
                    // the original 4 source cells get pushed past it.
                    cell {}
                    cell {}
                    cell { paragraph { text("Hello") } }
                    cell { verticalMerge(VerticalMerge.CONTINUE) }
                    cell {}
                }
                row {
                    cell {}
                    cell {}
                    cell {}
                    cell {}
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.png",
        "word/media/image2.jpg",
    )
}
