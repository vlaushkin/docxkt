package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo83SettingLanguagesTest : DocxFixtureTest("demo-83-setting-languages") {

    override fun build(): Document = document {
        paragraph {
            text("Yo vivo en Granada, una ciudad pequeña que tiene monumentos muy importantes como la Alhambra. Aquí la comida es deliciosa y son famosos el gazpacho, el rebujito y el salmorejo.")
        }
        paragraph {
            styleReference = "frenchNormal"
            text("Toute personne a droit à l'éducation. L'éducation doit être gratuite, au moins en ce qui concerne l'enseignement élémentaire et fondamental. L'enseignement élémentaire est obligatoire. L'enseignement technique et professionnel doit être généralisé; l'accès aux études supérieures doit être ouvert en pleine égalité à tous en fonction de leur mérite.")
        }
        paragraph {
            styleReference = "koreanNormal"
            text("대법관은 대법원장의 제청으로 국회의 동의를 얻어 대통령이 임명한다. 강화조약. 국가는 국민 모두의 생산 및 생활의 기반이 되는 국토의 효율적이고 균형있는 이용·개발과 보전을 위하여 법률이 정하는 바에 의하여 그에 관한 필요한 제한과 의무를 과할 수 있다, 국가는 청원에 대하여 심사할 의무를 진다.")
        }
    }
}
