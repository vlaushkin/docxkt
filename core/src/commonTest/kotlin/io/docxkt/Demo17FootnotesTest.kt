package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo17FootnotesTest : DocxFixtureTest("demo-17-footnotes") {

    override fun build(): Document = document {
        // Footnote bodies live in word/footnotes.xml (not compared).
        // We register simple stand-ins so the references resolve.
        footnote(1) { paragraph { text("Foo") }; paragraph { text("Bar") } }
        footnote(2) { paragraph { text("Stand-in for footnote 2") } }
        footnote(3) { paragraph { text("Stand-in for footnote 3") } }
        footnote(4) { paragraph { text("Foo1") } }
        footnote(5) { paragraph { text("Test1") } }
        footnote(6) { paragraph { text("My amazing reference1") } }

        paragraph {
            text("Hello")
            footnoteReference(1)
            text(" World!")
            footnoteReference(2)
            text(" GitHub!")
        }
        paragraph {
            text("Hello World")
            footnoteReference(3)
        }
        sectionBreak {}
        paragraph {
            text("Hello")
            footnoteReference(4)
            text(" World!")
            footnoteReference(5)
        }
        paragraph {
            text("Hello World Again")
            footnoteReference(6)
        }
    }
}
