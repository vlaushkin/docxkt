package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.run.PositionalTabAlignment
import io.docxkt.model.paragraph.run.PositionalTabLeader
import io.docxkt.model.paragraph.run.PositionalTabRelativeTo
import io.docxkt.testing.DocxFixtureTest

internal class Demo84PositionalTabsTest : DocxFixtureTest("demo-84-positional-tabs") {

    override fun build(): Document = document {
        paragraph {
            text("Full name")
            text("John Doe") {
                bold = true
                positionalTab(
                    alignment = PositionalTabAlignment.RIGHT,
                    relativeTo = PositionalTabRelativeTo.MARGIN,
                    leader = PositionalTabLeader.DOT,
                )
            }
        }
        paragraph {
            text("Hello World")
            text("Foo bar") {
                bold = true
                positionalTab(
                    alignment = PositionalTabAlignment.CENTER,
                    relativeTo = PositionalTabRelativeTo.INDENT,
                    leader = PositionalTabLeader.HYPHEN,
                )
            }
        }
    }
}
