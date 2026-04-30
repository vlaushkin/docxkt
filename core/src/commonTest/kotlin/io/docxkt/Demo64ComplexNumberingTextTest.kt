package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class Demo64ComplexNumberingTextTest : DocxFixtureTest("demo-64-complex-numbering-text") {

    override fun build(): Document = document {
        listTemplate("ref1") {
            level(level = 0, format = LevelFormat.DECIMAL, text = "%1")
            level(level = 1, format = LevelFormat.DECIMAL, text = "%1.%2")
            level(level = 2, format = LevelFormat.DECIMAL, text = "%1.%2.%3")
        }
        // Source order: lvl:0, lvl:1, lvl:2, lvl:0 x3, "Random text",
        // inst:1 lvl:0, inst:0 lvl:0 x2.
        paragraph {
            numbering("ref1", level = 0)
            text("REF1 - lvl:0")
        }
        paragraph {
            numbering("ref1", level = 1)
            text("REF1 - lvl:1")
        }
        paragraph {
            numbering("ref1", level = 2)
            text("REF1  - lvl:2")
        }
        repeat(3) {
            paragraph {
                numbering("ref1", level = 0)
                text("REF1 - lvl:0")
            }
        }
        paragraph { text("Random text") }
        paragraph {
            numbering("ref1", level = 0, instance = 1)
            text("REF1 - inst:1 - lvl:0")
        }
        paragraph {
            numbering("ref1", level = 0, instance = 0)
            text("REF1 - inst:0 - lvl:0")
        }
        paragraph {
            numbering("ref1", level = 0, instance = 0)
            text("REF1 - inst:0 - lvl:0")
        }
    }
}
