package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunLanguageVariantsTest : DocxFixtureTest("run-language-variants") {

    override fun build(): Document = document {
        paragraph {
            text("full") {
                language(value = "en-US", eastAsia = "ja-JP", bidirectional = "ar-SA")
            }
            text("partial") {
                language(value = "en-US")
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}
