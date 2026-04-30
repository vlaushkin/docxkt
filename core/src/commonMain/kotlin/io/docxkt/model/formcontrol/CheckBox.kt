// Port of: src/file/checkbox/checkbox.ts +
//          src/file/checkbox/checkbox-util.ts +
//          src/file/checkbox/checkbox-symbol.ts
package io.docxkt.model.formcontrol

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * One state (checked or unchecked) of a [CheckBox] — a
 * symbol character + the font that renders it.
 *
 * Upstream's `ICheckboxSymbolProperties` shape. Defaults
 * match upstream's hardcoded constants: `"2612"` ☒ for
 * checked, `"2610"` ☐ for unchecked, font `"MS Gothic"`.
 */
public data class CheckBoxState(
    val symbolHex: String,
    val font: String = "MS Gothic",
) {
    public companion object {
        public val DEFAULT_CHECKED: CheckBoxState =
            CheckBoxState(symbolHex = "2612", font = "MS Gothic")
        public val DEFAULT_UNCHECKED: CheckBoxState =
            CheckBoxState(symbolHex = "2610", font = "MS Gothic")
    }
}

/**
 * `<w:sdt>` content control wrapping a `<w14:checkbox>` —
 * a tickable form control.
 *
 * Wire shape:
 * ```
 * <w:sdt>
 *   <w:sdtPr>
 *     <w:alias w:val="…"/>?
 *     <w14:checkbox>
 *       <w14:checked w14:val="0"|"1"/>
 *       <w14:checkedState w14:val="…" w14:font="…"/>
 *       <w14:uncheckedState w14:val="…" w14:font="…"/>
 *     </w14:checkbox>
 *   </w:sdtPr>
 *   <w:sdtContent>
 *     <w:r><w:sym w:char="…" w:font="…"/></w:r>
 *   </w:sdtContent>
 * </w:sdt>
 * ```
 *
 * The rendered `<w:sym>` inside `<w:sdtContent>` mirrors
 * the [checkedState] when [checked] is true, the
 * [uncheckedState] otherwise.
 *
 * `<w14:checkedState>` and `<w14:uncheckedState>` are emitted
 * unconditionally (with defaults if user didn't override) —
 * matches upstream's `CheckBoxUtil` always-emit behaviour.
 */
internal class CheckBox(
    val checked: Boolean = false,
    val alias: String? = null,
    val checkedState: CheckBoxState = CheckBoxState.DEFAULT_CHECKED,
    val uncheckedState: CheckBoxState = CheckBoxState.DEFAULT_UNCHECKED,
) : XmlComponent("w:sdt") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:sdt")

        // sdtPr
        out.openElement("w:sdtPr")
        alias?.let { out.selfClosingElement("w:alias", "w:val" to it) }
        out.openElement("w14:checkbox")
        out.selfClosingElement("w14:checked", "w14:val" to if (checked) "1" else "0")
        emitState(out, "w14:checkedState", checkedState)
        emitState(out, "w14:uncheckedState", uncheckedState)
        out.closeElement("w14:checkbox")
        out.closeElement("w:sdtPr")

        // sdtContent — single run with symbol matching current state.
        val activeState = if (checked) checkedState else uncheckedState
        out.openElement("w:sdtContent")
        out.openElement("w:r")
        out.selfClosingElement(
            "w:sym",
            "w:char" to activeState.symbolHex,
            "w:font" to activeState.font,
        )
        out.closeElement("w:r")
        out.closeElement("w:sdtContent")

        out.closeElement("w:sdt")
    }

    private fun emitState(out: Appendable, tag: String, state: CheckBoxState) {
        out.selfClosingElement(
            tag,
            "w14:val" to state.symbolHex,
            "w14:font" to state.font,
        )
    }
}
