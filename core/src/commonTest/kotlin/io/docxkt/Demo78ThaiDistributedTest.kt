package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo78ThaiDistributedTest : DocxFixtureTest("demo-78-thai-distributed") {

    override fun build(): Document = document {
        paragraph {
            alignment = AlignmentType.THAI_DISTRIBUTE
            text("บริษัท บิสกิด จำกัด (บริษัทฯ) ได้จดทะเบียนจัดตั้งขึ้นเป็นบริษัทจำกัดตามประมวลกฎหมายแพ่งและพาณิชย์ของประเทศไทย เมื่อวันที่ 30 พฤษภาคม 2561 ทะเบียนนิติบุคคลเลขที่ 0845561005665") {
                size = 36
            }
        }
    }
}
