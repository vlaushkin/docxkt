package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.run.EmphasisMark
import io.docxkt.model.paragraph.run.TextEffect
import io.docxkt.testing.DocxFixtureTest

internal class RunTextEffectsTest : DocxFixtureTest("run-text-effects") {

    override fun build(): Document = document {
        paragraph {
            text("effects") {
                emboss = true
                imprint = false
                vanish = true
                textEffect = TextEffect.SHIMMER
                emphasisMark = EmphasisMark.DOT
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}
