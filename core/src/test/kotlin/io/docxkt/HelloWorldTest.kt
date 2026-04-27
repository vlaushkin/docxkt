// A minimal "Hello, world!" document matches the golden fixture under
// XMLUnit normalization for the three compared parts.
package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HelloWorldTest : DocxFixtureTest("hello-world") {

    override fun build(): Document = document {
        paragraph { text("Hello, world!") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "[Content_Types].xml",
        "_rels/.rels",
    )
}
