package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo52JapaneseTest : DocxFixtureTest("demo-52-japanese") {

    override fun build(): Document = document {
        paragraph {
            styleReference = "Heading1"
            text("KFCを食べるのが好き")
        }
        paragraph { text("こんにちは") }
    }
}
