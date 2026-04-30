package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo13XmlStylesTest : DocxFixtureTest("demo-13-xml-styles") {

    override fun build(): Document = document {
        paragraph {
            styleReference = "Heading1"
            text("Cool Heading Text")
        }
        paragraph {
            styleReference = "MyFancyStyle"
            text("This is a custom named style from the template \"MyFancyStyle\"")
        }
        paragraph { text("Some normal text") }
        paragraph {
            styleReference = "MyFancyStyle"
            text("MyFancyStyle again")
        }
    }
}
