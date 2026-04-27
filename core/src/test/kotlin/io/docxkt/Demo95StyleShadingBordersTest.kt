package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo95StyleShadingBordersTest : DocxFixtureTest("demo-95-style-shading-borders") {

    override fun build(): Document = document {
        paragraph {
            styleReference = "withSingleBlackBordersAndYellowShading"
            text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
        }
    }
}
