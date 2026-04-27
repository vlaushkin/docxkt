package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo80ThaiDistributedTest : DocxFixtureTest("demo-80-thai-distributed") {

    override fun build(): Document = document {
        margins(top = 0, right = 1360, bottom = 1360, left = 1360)
        paragraph {
            alignment = AlignmentType.THAI_DISTRIBUTE
            text("บริษัทฯ มีเงินสด 41,985.00 บาท และ 25,855.66 บาทตามลำดับ เงินสดทั้งจำนวนอยู่ในความดูแลและรับผิดชอบของกรรมการ บริษัทฯบันทึกการรับชำระเงินและการจ่ายชำระเงินผ่านบัญชีเงินสดเพียงเท่านั้น ซึ่งอาจกระทบต่อความถูกต้องครบถ้วนของการบันทึกบัญชี ทั้งนี้ขึ้นอยู่กับระบบการควบคุมภายในของบริษัท") {
                size = 28
            }
        }
    }
}
