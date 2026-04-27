package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.AnchorMargins
import io.docxkt.model.drawing.HorizontalRelativeFrom
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.drawing.VerticalRelativeFrom
import io.docxkt.model.drawing.WrapSide
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.FixtureLoader

internal class Demo38TextWrappingTest : DocxFixtureTest("demo-38-text-wrapping") {

    override fun build(): Document {
        val pizza = FixtureLoader.loadBinaryPart(
            fixtureName = "demo-38-text-wrapping",
            partPath = "word/media/image1.gif",
        )
        return document {
            paragraph { text(LOREM_1) }
            paragraph { text(LOREM_2) }
            paragraph { text(LOREM_3) }
            paragraph {
                imageAnchor(
                    bytes = pizza,
                    widthEmus = 1_905_000,
                    heightEmus = 1_905_000,
                    format = ImageFormat.GIF,
                ) {
                    positionH(HorizontalRelativeFrom.PAGE, offsetEmus = 2_014_400)
                    positionV(VerticalRelativeFrom.PAGE, offsetEmus = 2_014_400)
                    wrapSquare(
                        side = WrapSide.BOTH_SIDES,
                        marginTopEmus = 201_440,
                        marginBottomEmus = 201_440,
                    )
                    anchorMargins = AnchorMargins(
                        topEmus = 201_440,
                        bottomEmus = 201_440,
                    )
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.gif",
    )

    private companion object {
        const val LOREM_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vehicula nec nulla vitae efficitur. Ut interdum mauris eu ipsum rhoncus, nec pharetra velit placerat. Sed vehicula libero ac urna molestie, id pharetra est pellentesque. Praesent iaculis vehicula fringilla. Duis pretium gravida orci eu vestibulum. Mauris tincidunt ipsum dolor, ut ornare dolor pellentesque id. Integer in nulla gravida, lacinia ante non, commodo ex. Vivamus vulputate nisl id lectus finibus vulputate. Ut et nisl mi. Cras fermentum augue arcu, ac accumsan elit euismod id. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed ac posuere nisi. Pellentesque tincidunt vehicula bibendum. Phasellus eleifend viverra nisl."
        const val LOREM_2 = "Proin ac purus faucibus, porttitor magna ut, cursus nisl. Vivamus ante purus, porta accumsan nibh eget, eleifend dignissim odio. Integer sed dictum est, aliquam lacinia justo. Donec ultrices auctor venenatis. Etiam interdum et elit nec elementum. Pellentesque nec viverra mauris. Etiam suscipit leo nec velit fringilla mattis. Pellentesque justo lacus, sodales eu condimentum in, dapibus finibus lacus. Morbi vitae nibh sit amet sem molestie feugiat. In non porttitor enim."
        const val LOREM_3 = "Ut eget diam cursus quam accumsan interdum at id ante. Ut mollis mollis arcu, eu scelerisque dui tempus in. Quisque aliquam, augue quis ornare aliquam, ex purus ultrices mauris, ut porta dolor dolor nec justo. Nunc a tempus odio, eu viverra arcu. Suspendisse vitae nibh nec mi pharetra tempus. Mauris ut ullamcorper sapien, et sagittis sapien. Vestibulum in urna metus. In scelerisque, massa id bibendum tempus, quam orci rutrum turpis, a feugiat nisi ligula id metus. Praesent id dictum purus. Proin interdum ipsum nulla."
    }
}
