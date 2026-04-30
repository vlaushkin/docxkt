package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureXml

internal class Demo53ChineseTest : DocxFixtureTest("demo-53-chinese") {

    override fun build(): Document {
        val longText = loadFixtureXml("demo-53-chinese", "lastparagraph.txt")
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
