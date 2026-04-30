package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class Demo46ShadingTextTest : DocxFixtureTest("demo-46-shading-text") {

    override fun build(): Document = document {
        header {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("Hello World") {
                    color = "FF0000"
                    bold = true
                    size = 24
                    font(name = "Garamond")
                    shading(
                        pattern = ShadingPattern.REVERSE_DIAG_STRIPE,
                        color = "00FFFF",
                        fill = "FF0000",
                    )
                }
            }
            paragraph {
                shading(
                    pattern = ShadingPattern.DIAG_CROSS,
                    color = "00FFFF",
                    fill = "FF0000",
                )
                text("Hello World for entire paragraph")
            }
        }
        paragraph {
            text("Embossed text - hello world") { emboss = true }
            text("Imprinted text - hello world") { imprint = true }
        }
    }
}
