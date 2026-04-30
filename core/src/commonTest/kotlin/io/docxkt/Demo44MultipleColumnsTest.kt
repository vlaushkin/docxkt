package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureXml

internal class Demo44MultipleColumnsTest : DocxFixtureTest("demo-44-multiple-columns") {

    private fun loadResource(name: String): String =
        loadFixtureXml("demo-44-multiple-columns", "$name")

    override fun build(): Document {
        val lorem2col = loadResource("lorem-2col.txt")
        val lorem3col = loadResource("lorem-3col.txt")
        return document {
            paragraph { text("This text will be split into 2 columns on a page.") }
            paragraph { text(lorem2col) }
            sectionBreak {
                columns(count = 2, spaceTwips = 708)
            }
            paragraph { text("This text will be split into 3 columns on a page.") }
            paragraph { text(lorem3col) }
            sectionBreak {
                columns(count = 3, spaceTwips = 708)
            }
            // Document-level (trailing) section: 2 columns with separator.
            columns(count = 2, spaceTwips = 708, separator = true)
            paragraph { text("This text will be split into 2 columns on a page.") }
            paragraph { text(lorem2col) }
        }
    }
}
