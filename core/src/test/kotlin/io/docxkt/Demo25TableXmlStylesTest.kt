package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class Demo25TableXmlStylesTest : DocxFixtureTest("demo-25-table-xml-styles") {

    override fun build(): Document = document {
        table {
            styleReference = "MyCustomTableStyle"
            width(TableWidth.dxa(9070))
            row {
                cell { paragraph { text("Header Colum 1") } }
                cell { paragraph { text("Header Colum 2") } }
            }
            row {
                cell { paragraph { text("Column Content 3") } }
                cell { paragraph { text("Column Content 2") } }
            }
        }
    }
}
