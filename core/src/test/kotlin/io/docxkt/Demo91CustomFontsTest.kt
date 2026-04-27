package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo91CustomFontsTest : DocxFixtureTest("demo-91-custom-fonts") {

    override fun build(): Document {
        val ttf = javaClass
            .getResourceAsStream("/fixtures/demo-91-custom-fonts/Pacifico.ttf")!!
            .readBytes()
        return document {
            embedFont(name = "Pacifico", bytes = ttf)
            paragraph {
                runDefaults { font(name = "Pacifico") }
                text("Hello World")
                text("Foo Bar") {
                    bold = true
                    size = 40
                    font(name = "Pacifico")
                }
                text("Github is the best") {
                    bold = true
                    font(name = "Pacifico")
                    tab()
                }
            }
        }
    }

    override val comparedBinaryParts: List<String> = listOf("word/fonts/Pacifico.odttf")
}
