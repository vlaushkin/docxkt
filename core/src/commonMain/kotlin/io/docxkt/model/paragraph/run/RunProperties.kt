// Port of: src/file/paragraph/run/properties.ts (RunProperties, L245-L413).
package io.docxkt.model.paragraph.run

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.writeBorderSide
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.writeShading
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.onOff
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * Optional underline specifier: type plus an optional hex RGB color.
 */
public data class Underline(
    val type: UnderlineType,
    /** Hex RGB color (e.g. "FF0000"); leading '#' is stripped. */
    val color: String? = null,
)

/**
 * `<w:rPr>` — run properties.
 *
 * Unset (`null`) fields mean "inherit / not set". Setting any field
 * to a non-null value causes `<w:rPr>` to be emitted; with all-null
 * fields the component is skipped entirely — see
 * [IgnoreIfEmptyXmlComponent].
 *
 * Children are written in upstream's canonical order:
 * `rStyle → rFonts → b/bCs → i/iCs → caps → strike/dstrike → emboss
 * → imprint → noProof → snapToGrid → vanish → color → spacing
 * (characterSpacing) → w (scale) → kern → position → sz/szCs →
 * highlight/highlightCs → u → effect → bdr → shd → vertAlign → rtl
 * → em → lang`. Attribute order inside each child matches
 * upstream's `BuilderElement` definition.
 *
 * **`vanish` quirk:** upstream emits `<w:vanish/>` only when the
 * value is truthy (no `w:val="false"` form). Every other OnOff in
 * this class uses full OnOff semantics.
 */
internal class RunProperties(
    val styleReference: String? = null,
    val bold: Boolean? = null,
    val italics: Boolean? = null,
    val strike: Boolean? = null,
    val doubleStrike: Boolean? = null,
    /**
     * smallCaps and allCaps are mutually exclusive — upstream's
     * `properties.ts` writes smallCaps first and skips allCaps if both
     * are set. We match.
     */
    val smallCaps: Boolean? = null,
    val allCaps: Boolean? = null,
    val emboss: Boolean? = null,
    val imprint: Boolean? = null,
    val noProof: Boolean? = null,
    val snapToGrid: Boolean? = null,
    /** Quirk: truthy-only emission. `false` skips the element like `null`. */
    val vanish: Boolean? = null,
    val superScript: Boolean? = null,
    val subScript: Boolean? = null,
    val underline: Underline? = null,
    /** Hex RGB color (e.g. "FF0000") or the literal "auto". */
    val color: String? = null,
    /** Character spacing in twips (signed). Emits `<w:spacing w:val="N"/>`. */
    val characterSpacing: Int? = null,
    /** Character-scale percentage. `100` = 100%. Emits `<w:w w:val="N"/>`. */
    val scale: Int? = null,
    /** Kerning threshold in half-points. Emits `<w:kern w:val="N"/>`. */
    val kern: Int? = null,
    /**
     * Raise/lower from baseline. Passes through verbatim — upstream does
     * no unit parsing. Typical values: `"6pt"`, `"-3pt"`.
     */
    val position: String? = null,
    /**
     * Font size in **half-points**. OOXML quirk: `size = 24` means 12 pt.
     * Do not multiply at the DSL layer — keep the value wire-close.
     */
    val size: Int? = null,
    val font: Font? = null,
    val highlight: HighlightColor? = null,
    val textEffect: TextEffect? = null,
    val border: BorderSide? = null,
    val shading: Shading? = null,
    val rightToLeft: Boolean? = null,
    val emphasisMark: EmphasisMark? = null,
    val language: Language? = null,
    /**
     * `<w:rPrChange>` revision wrapper. When non-null, emits
     * `<w:rPrChange w:id w:author w:date>` as the LAST child of
     * `<w:rPr>`, carrying [revisionOldProps] inside as a nested
     * `<w:rPr>`. Mirrors upstream's `RunPropertiesChange`.
     */
    val revision: io.docxkt.model.revision.RevisionInfo? = null,
    val revisionOldProps: RunProperties? = null,
) : IgnoreIfEmptyXmlComponent("w:rPr") {

    override fun isEmpty(): Boolean = (
        styleReference == null && bold == null && italics == null &&
            strike == null && doubleStrike == null &&
            smallCaps == null && allCaps == null &&
            emboss == null && imprint == null && noProof == null &&
            snapToGrid == null && vanish != true &&
            superScript == null && subScript == null &&
            underline == null && color == null &&
            characterSpacing == null && scale == null && kern == null && position == null &&
            size == null && font == null && highlight == null &&
            textEffect == null && border == null && shading == null &&
            rightToLeft == null && emphasisMark == null &&
            (language == null || language.isEmpty()) &&
            revision == null
    )

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:rPr")

        styleReference?.let {
            out.selfClosingElement("w:rStyle", "w:val" to it)
        }

        font?.let { out.writeFont(it) }

        // bold + bCs (complex-script mirror upstream emits whenever b is set).
        out.onOff("w:b", bold)
        if (bold != null) out.onOff("w:bCs", bold)

        // italics + iCs — same mirror treatment.
        out.onOff("w:i", italics)
        if (italics != null) out.onOff("w:iCs", italics)

        // smallCaps / allCaps mutually exclusive — smallCaps wins when both set.
        if (smallCaps != null) {
            out.onOff("w:smallCaps", smallCaps)
        } else if (allCaps != null) {
            out.onOff("w:caps", allCaps)
        }

        out.onOff("w:strike", strike)
        out.onOff("w:dstrike", doubleStrike)

        out.onOff("w:emboss", emboss)
        out.onOff("w:imprint", imprint)
        out.onOff("w:noProof", noProof)
        out.onOff("w:snapToGrid", snapToGrid)
        if (vanish == true) out.selfClosingElement("w:vanish")

        color?.let { out.selfClosingElement("w:color", "w:val" to normalizeHex(it)) }

        characterSpacing?.let {
            out.selfClosingElement("w:spacing", "w:val" to it.toString())
        }
        scale?.let {
            out.selfClosingElement("w:w", "w:val" to it.toString())
        }
        kern?.let {
            out.selfClosingElement("w:kern", "w:val" to it.toString())
        }
        position?.let {
            out.selfClosingElement("w:position", "w:val" to it)
        }

        size?.let {
            out.selfClosingElement("w:sz", "w:val" to it.toString())
            out.selfClosingElement("w:szCs", "w:val" to it.toString())
        }

        highlight?.let {
            out.selfClosingElement("w:highlight", "w:val" to it.wire)
            out.selfClosingElement("w:highlightCs", "w:val" to it.wire)
        }

        underline?.let {
            out.selfClosingElement(
                "w:u",
                "w:val" to it.type.wire,
                "w:color" to it.color?.let(::normalizeHex),
            )
        }

        textEffect?.let {
            out.selfClosingElement("w:effect", "w:val" to it.wire)
        }

        border?.let { writeBorderSide(out, "w:bdr", it) }
        shading?.let { writeShading(out, it) }

        if (subScript == true) out.selfClosingElement("w:vertAlign", "w:val" to "subscript")
        if (superScript == true) out.selfClosingElement("w:vertAlign", "w:val" to "superscript")

        out.onOff("w:rtl", rightToLeft)

        emphasisMark?.let {
            out.selfClosingElement("w:em", "w:val" to it.wire)
        }

        language?.takeUnless { it.isEmpty() }?.let {
            out.selfClosingElement(
                "w:lang",
                "w:val" to it.value,
                "w:eastAsia" to it.eastAsia,
                "w:bidi" to it.bidirectional,
            )
        }

        // <w:rPrChange> — last child of <w:rPr>, carries the OLD
        // <w:rPr> inside.
        revision?.let {
            out.openElement("w:rPrChange", *it.attrs())
            (revisionOldProps ?: RunProperties()).appendXml(out)
            out.closeElement("w:rPrChange")
        }

        out.closeElement("w:rPr")
    }

    private fun Appendable.writeFont(font: Font) {
        selfClosingElement(
            "w:rFonts",
            "w:ascii" to font.ascii,
            "w:cs" to font.cs,
            "w:eastAsia" to font.eastAsia,
            "w:hAnsi" to font.hAnsi,
            "w:hint" to font.hint,
        )
    }

    /**
     * Strip a leading `#` but otherwise pass through; matches upstream's
     * `hexColorValue`. (Full validation is out of scope.)
     */
    private fun normalizeHex(value: String): String =
        if (value.startsWith("#")) value.substring(1) else value
}
