package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.SectionType
import io.docxkt.testing.DocxFixtureTest

internal class Demo58SectionTypesTest : DocxFixtureTest("demo-58-section-types") {

    override fun build(): Document = document {
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
        }
        sectionBreak {}
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
        }
        sectionBreak { type(SectionType.CONTINUOUS) }
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
        }
        sectionBreak { type(SectionType.ODD_PAGE) }
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
        }
        sectionBreak { type(SectionType.EVEN_PAGE) }
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
        }
        sectionType(SectionType.NEXT_PAGE)
    }
}
