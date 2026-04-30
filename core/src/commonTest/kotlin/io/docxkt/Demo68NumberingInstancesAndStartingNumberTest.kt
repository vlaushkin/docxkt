package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class Demo68NumberingInstancesAndStartingNumberTest :
    DocxFixtureTest("demo-68-numbering-instances-and-starting-number") {

    override fun build(): Document = document {
        listTemplate("ref1") {
            level(level = 0, format = LevelFormat.DECIMAL, text = "%1", start = 10)
        }
        listTemplate("ref2") {
            level(level = 0, format = LevelFormat.DECIMAL, text = "%1")
        }
        // ref1 instance 0: 2 paragraphs
        paragraph {
            numbering("ref1", level = 0, instance = 0)
            text("REF1 - inst:0 - lvl:0")
        }
        paragraph {
            numbering("ref1", level = 0, instance = 0)
            text("REF1 - inst:0 - lvl:0")
        }
        // ref1 instance 1: 2 paragraphs
        paragraph {
            numbering("ref1", level = 0, instance = 1)
            text("REF1 - inst:1 - lvl:0")
        }
        paragraph {
            numbering("ref1", level = 0, instance = 1)
            text("REF1 - inst:1 - lvl:0")
        }
        // ref2 instance 1: 2 paragraphs (notice upstream uses inst=1, not 0)
        paragraph {
            numbering("ref2", level = 0, instance = 1)
            text("REF2 - inst:0 - lvl:0")
        }
        paragraph {
            numbering("ref2", level = 0, instance = 1)
            text("REF2 - inst:0 - lvl:0")
        }
    }
}
