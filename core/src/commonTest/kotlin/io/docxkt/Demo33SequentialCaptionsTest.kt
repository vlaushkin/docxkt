package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo33SequentialCaptionsTest : DocxFixtureTest("demo-33-sequential-captions") {

    override fun build(): Document = document {
        paragraph {
            text("Hello World 1->")
            sequentialIdentifier("Caption")
            text(" text after sequencial caption 2->")
            sequentialIdentifier("Caption")
        }
        paragraph {
            text("Hello World 1->")
            sequentialIdentifier("Label")
            text(" text after sequencial caption 2->")
            sequentialIdentifier("Label")
        }
        paragraph {
            text("Hello World 1->")
            sequentialIdentifier("Another")
            text(" text after sequencial caption 3->")
            sequentialIdentifier("Label")
        }
        paragraph {
            text("Hello World 2->")
            sequentialIdentifier("Another")
            text(" text after sequencial caption 4->")
            sequentialIdentifier("Label")
        }
    }
}
