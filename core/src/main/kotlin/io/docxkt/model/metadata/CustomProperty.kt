// Port of: src/file/custom-properties/custom-property.ts.
package io.docxkt.model.metadata

/**
 * Single `<property>` entry inside `docProps/custom.xml`.
 *
 * [pid] is assigned at assembly time — upstream starts at 2 and
 * increments per entry in DSL-declaration order. [formatId] is
 * a fixed well-known GUID upstream also hardcodes.
 */
internal data class CustomProperty(
    val name: String,
    val value: String,
) {
    companion object {
        /** Fixed per upstream's `CustomProperty` constructor. */
        const val FORMAT_ID: String = "{D5CDD505-2E9C-101B-9397-08002B2CF9AE}"

        /** Upstream starts pid at 2; "I'm not sure why, but every example I have seen starts with 2". */
        const val PID_START: Int = 2
    }
}
