package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo97EndnotesTest : DocxFixtureTest("demo-97-endnotes") {

    override fun build(): Document = document {
        // Endnotes content is in word/endnotes.xml (not compared);
        // however, references in the body still need to be registered
        // so the DSL accepts them.
        endnote(1) { paragraph { text("This is the first endnote with some detailed explanation.") } }
        endnote(2) {
            paragraph { text("Second endnote") }
            paragraph { text("With multiple paragraphs for more complex content.") }
        }
        endnote(3) { paragraph { text("Third endnote referencing important source material.") } }
        endnote(4) { paragraph { text("Fourth endnote from a different section.") } }

        paragraph {
            spacing(after = 400)
            text("Endnotes Demo Document") {
                bold = true
                size = 28
            }
        }
        paragraph {
            spacing(after = 200)
            text("This document demonstrates endnotes functionality. ")
            text("Here is some text with an endnote reference")
            endnoteReference(1)
            text(". This allows for detailed citations and references ")
            endnoteReference(2)
            text(" without cluttering the main text flow.")
        }
        paragraph {
            spacing(after = 200)
            text("Endnotes appear at the end of the document, ")
            text("unlike footnotes which appear at the bottom of each page")
            endnoteReference(3)
            text(". This makes them ideal for academic papers and formal documents.")
        }
        sectionBreak {
            // section 1 ends here with default page setup.
        }
        paragraph {
            spacing(after = 200)
            text("Second Section") {
                bold = true
                size = 24
            }
        }
        paragraph {
            text("This is content from a different section ")
            text("with its own endnote reference")
            endnoteReference(4)
            text(". Endnotes from all sections appear together at the document end.")
        }
    }
}
