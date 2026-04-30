package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo51CharacterStylesTest : DocxFixtureTest("demo-51-character-styles") {

    override fun build(): Document = document {
        paragraph {
            text("Foo bar") { styleReference = "myRedStyle" }
        }
        paragraph {
            text("First Word") { styleReference = "strong" }
            text(" - Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
        }
    }
}
