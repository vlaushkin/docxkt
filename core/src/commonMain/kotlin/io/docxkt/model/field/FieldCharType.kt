// Port of: src/file/paragraph/run/field.ts (FieldCharacterType enum).
package io.docxkt.model.field

internal enum class FieldCharType(val wire: String) {
    BEGIN("begin"),
    SEPARATE("separate"),
    END("end"),
}
