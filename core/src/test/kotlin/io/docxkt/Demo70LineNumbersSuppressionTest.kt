package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.LineNumberRestart
import io.docxkt.testing.DocxFixtureTest

internal class Demo70LineNumbersSuppressionTest : DocxFixtureTest("demo-70-line-numbers-suppression") {

    private val p1 = "Himenaeos duis luctus nullam fermentum lobortis potenti vivamus non dis, sed facilisis ultricies scelerisque aenean risus hac senectus. Adipiscing id venenatis justo ante gravida placerat, ac curabitur dis pellentesque proin bibendum risus, aliquam porta taciti vulputate primis. Tortor ipsum fermentum quam vel convallis primis nisl praesent tincidunt, lobortis quisque felis vitae condimentum class ut sem nam, aenean potenti pretium ac amet lacinia himenaeos mi. Aliquam nisl turpis hendrerit est morbi malesuada, augue interdum mus inceptos curabitur tristique, parturient feugiat sodales nulla facilisi. Aliquam non pulvinar purus nulla ex integer, velit faucibus vitae at bibendum quam, risus elit aenean adipiscing posuere."

    private val p2 = "Enim mollit nostrud ut dolor eiusmod id sit occaecat dolore culpa amet. Veniam dolor consequat dolor labore ullamco laborum dolore eiusmod qui adipisicing. Elit nulla cupidatat et magna. Id eiusmod tempor non laborum ipsum. Veniam et aliqua excepteur duis officia enim elit excepteur fugiat duis. Sit sunt ullamco non dolor est qui deserunt consequat magna. Esse pariatur esse dolor ut excepteur dolor nisi nisi non est cupidatat mollit."

    private val p3 = "Sed laoreet id mattis egestas nam mollis elit lacinia convallis dui tincidunt ultricies habitant, pharetra per maximus interdum neque tempor risus efficitur morbi imperdiet senectus. Lectus laoreet senectus finibus inceptos donec potenti fermentum, ultrices eleifend odio suscipit magnis tellus maximus nibh, ac sit nullam eget felis himenaeos. Diam class sem magnis aenean commodo faucibus id proin mi, nullam sodales nec mus parturient ornare ad inceptos velit hendrerit, bibendum placerat eleifend integer facilisis urna dictumst suspendisse."

    override fun build(): Document = document {
        lineNumbering(countBy = 1, restart = LineNumberRestart.CONTINUOUS)
        paragraph {
            styleReference = "Heading1"
            text("Hello")
        }
        paragraph { text(p1) }
        paragraph {
            suppressLineNumbers = true
            text(p2)
        }
        paragraph { text(p3) }
    }
}
