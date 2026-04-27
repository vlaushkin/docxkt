package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo92DeclarativeCustomFontsTest : DocxFixtureTest("demo-92-declarative-custom-fonts") {

    override fun build(): Document {
        val ttf = javaClass
            .getResourceAsStream("/fixtures/demo-92-declarative-custom-fonts/Pacifico.ttf")!!
            .readBytes()
        return document {
            embedFont(name = "Pacifico", bytes = ttf)
            paragraph {
                text("Hello World")
                text("Foo Bar") {
                    bold = true
                    size = 40
                }
                text("Github is the best") {
                    bold = true
                    tab()
                }
            }
        }
    }

    override val comparedBinaryParts: List<String> = listOf("word/fonts/Pacifico.odttf")
}
