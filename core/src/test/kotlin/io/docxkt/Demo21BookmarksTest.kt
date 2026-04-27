package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo21BookmarksTest : DocxFixtureTest("demo-21-bookmarks") {

    private val lorem =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam mi velit, convallis convallis scelerisque nec, faucibus nec leo. Phasellus at posuere mauris, tempus dignissim velit. Integer et tortor dolor. Duis auctor efficitur mattis. Vivamus ut metus accumsan tellus auctor sollicitudin venenatis et nibh. Cras quis massa ac metus fringilla venenatis. Proin rutrum mauris purus, ut suscipit magna consectetur id. Integer consectetur sollicitudin ante, vitae faucibus neque efficitur in. Praesent ultricies nibh lectus. Mauris pharetra id odio eget iaculis. Duis dictum, risus id pellentesque rutrum, lorem quam malesuada massa, quis ullamcorper turpis urna a diam. Cras vulputate metus vel massa porta ullamcorper. Etiam porta condimentum nulla nec tristique. Sed nulla urna, pharetra non tortor sed, sollicitudin molestie diam. Maecenas enim leo, feugiat eget vehicula id, sollicitudin vitae ante."

    override fun build(): Document = document {
        footer {
            paragraph {
                internalHyperlink("myAnchorId") {
                    text("Click here!") { styleReference = "Hyperlink" }
                }
            }
        }

        paragraph {
            styleReference = "Heading1"
            bookmark("myAnchorId") { text("Lorem Ipsum") }
        }
        paragraph { text("\n") }
        paragraph { text(lorem) }
        paragraph { pageBreak() }
        paragraph {
            internalHyperlink("myAnchorId") {
                text("Styled") {
                    styleReference = "Hyperlink"
                    bold = true
                }
                text(" Anchor Text") { styleReference = "Hyperlink" }
            }
        }
        paragraph {
            text("The bookmark can be seen on page ")
            pageReference("myAnchorId")
        }
    }
}
