package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo53ChineseTest : DocxFixtureTest("demo-53-chinese") {

    override fun build(): Document {
        val longText = javaClass.getResourceAsStream(
            "/fixtures/demo-53-chinese/lastparagraph.txt"
        )!!.bufferedReader(Charsets.UTF_8).readText()
        return document {
            paragraph {
                styleReference = "Heading1"
                text("中文和英文 Chinese and English")
            }
            paragraph {
                text("中文和英文 Chinese and English")
            }
            paragraph {
                text("中文和英文 Chinese and English") {
                    font(eastAsia = "SimSun")
                }
            }
            paragraph {
                text(longText)
            }
        }
    }
}
