package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.LineRule
import io.docxkt.testing.DocxFixtureTest

internal class Demo62ParagraphSpacingTest : DocxFixtureTest("demo-62-paragraph-spacing") {

    override fun build(): Document = document {
        paragraph {
            spacing(after = 5000, before = 5000)
            text("Hello World")
        }
        paragraph {
            spacing(line = 2000, lineRule = LineRule.AUTO)
            text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed turpis ex, aliquet et faucibus quis, euismod in odio. Fusce gravida tempor nunc sed lacinia. Nulla sed dolor fringilla, fermentum libero ut, egestas ex. Donec pellentesque metus non orci lacinia bibendum. Cras porta ex et mollis hendrerit. Suspendisse id lectus suscipit, elementum lacus eu, convallis felis. Fusce sed bibendum dolor, id posuere ligula. Aliquam eu elit ut urna eleifend vestibulum. Praesent condimentum at turpis sed scelerisque. Suspendisse porttitor metus nec vestibulum egestas. Sed in eros sapien. Morbi efficitur placerat est a consequat. Nunc bibendum porttitor mi nec tempus. Morbi dictum augue porttitor nisi sodales sodales.")
        }
    }
}
