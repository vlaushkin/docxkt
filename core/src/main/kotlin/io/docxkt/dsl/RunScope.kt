// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.border.BorderSide
import io.docxkt.model.drawing.Drawing
import io.docxkt.model.drawing.Image
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.field.FieldChar
import io.docxkt.model.field.FieldCharType
import io.docxkt.model.field.InstrText
import io.docxkt.model.paragraph.run.Break
import io.docxkt.model.paragraph.run.BreakType
import io.docxkt.model.paragraph.run.EmphasisMark
import io.docxkt.model.paragraph.run.Font
import io.docxkt.model.paragraph.run.HighlightColor
import io.docxkt.model.paragraph.run.Language
import io.docxkt.model.paragraph.run.RunProperties
import io.docxkt.model.paragraph.run.Tab
import io.docxkt.model.paragraph.run.TextEffect
import io.docxkt.model.paragraph.run.Underline
import io.docxkt.model.paragraph.run.UnderlineType
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.xml.XmlComponent

/**
 * Builder for run-level formatting — the scope supplied to
 * `text("...") { bold = true; size = 24 }`.
 *
 * Properties map 1:1 to [RunProperties] fields. Semantics:
 *
 *  - `null` (default) — property not set, Word inherits.
 *  - `true` / `false` — explicit on/off. Respects OOXML OnOff semantics
 *    through the [io.docxkt.xml.onOff] helper.
 *  - For [size], the value is OOXML **half-points** (`24` = 12 pt).
 *
 * Break-producing methods ([lineBreak], [pageBreak]) append `<w:br>`
 * children to the current run — so `text("before") { pageBreak() }`
 * emits `<w:r>...<w:t>before</w:t><w:br w:type="page"/></w:r>`.
 *
 */
@DocxktDsl
public class RunScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {

    public var bold: Boolean? = null
    public var italics: Boolean? = null
    public var strike: Boolean? = null
    public var doubleStrike: Boolean? = null
    public var smallCaps: Boolean? = null
    public var allCaps: Boolean? = null
    public var superScript: Boolean? = null
    public var subScript: Boolean? = null

    /** Hex RGB (e.g. `"FF0000"`) or the literal `"auto"`. Leading `#` is fine. */
    public var color: String? = null

    /** Font size in OOXML half-points. `24` = 12 pt. */
    public var size: Int? = null

    public var highlight: HighlightColor? = null

    /**
     * Reference to a character-style id (`<w:rStyle w:val="StyleId"/>`).
     * Emission-only — style *resolution* against a styles part is
     * not done at the DSL layer.
     */
    public var styleReference: String? = null

    public var emboss: Boolean? = null
    public var imprint: Boolean? = null
    public var noProof: Boolean? = null
    public var snapToGrid: Boolean? = null
    public var rightToLeft: Boolean? = null

    /**
     * Hidden text. **Quirk**: upstream emits `<w:vanish/>` only when
     * the value is truthy — no `w:val="false"` form. `false` and
     * `null` both skip the element.
     */
    public var vanish: Boolean? = null

    /** Character spacing in twips (signed). Emits `<w:spacing w:val="N"/>`. */
    public var characterSpacing: Int? = null

    /** Character-scale percentage. `100` = 100%. Emits `<w:w w:val="N"/>`. */
    public var scale: Int? = null

    /** Kerning threshold in half-points. Emits `<w:kern w:val="N"/>`. */
    public var kern: Int? = null

    /**
     * Raise/lower from baseline. Passes through verbatim — upstream
     * does no unit parsing. Typical values: `"6pt"`, `"-3pt"`.
     */
    public var position: String? = null

    public var textEffect: TextEffect? = null

    /**
     * Run-level border (`<w:bdr>`). **Note:** unlike table-cell or
     * paragraph borders, the run-level border is a single element
     * (one "side") — not a block of top/bottom/left/right. Pass a
     * single [BorderSide] directly.
     */
    public var border: BorderSide? = null

    public var emphasisMark: EmphasisMark? = null

    private var underlineValue: Underline? = null
    private var fontValue: Font? = null
    private var shadingValue: Shading? = null
    private var languageValue: Language? = null
    private val leadingChildren = mutableListOf<XmlComponent>()
    private val extraChildren = mutableListOf<XmlComponent>()

    /**
     * Set underline. Color is optional; when provided it is a hex RGB (or
     * "auto"). Calling [underline] again replaces the previous value.
     */
    public fun underline(type: UnderlineType, color: String? = null) {
        underlineValue = Underline(type = type, color = color)
    }

    /**
     * Set a single font name, applied to all four character ranges
     * (ascii, hAnsi, cs, eastAsia) — upstream's `createRunFonts("Arial")`
     * shape.
     */
    public fun font(name: String, hint: String? = null) {
        fontValue = Font.single(name, hint = hint)
    }

    /**
     * Set per-range fonts. Any range left `null` skips its attribute in
     * the emitted `<w:rFonts>`. Upstream's `IFontAttributesProperties`
     * shape.
     */
    public fun font(
        ascii: String? = null,
        hAnsi: String? = null,
        cs: String? = null,
        eastAsia: String? = null,
        hint: String? = null,
    ) {
        fontValue = Font.perScript(
            ascii = ascii, hAnsi = hAnsi, cs = cs, eastAsia = eastAsia, hint = hint,
        )
    }

    /** Run-level shading — same `<w:shd>` wire shape as table cells. */
    public fun shading(value: Shading) {
        shadingValue = value
    }

    /** Run-level shading from pattern + colors. */
    public fun shading(pattern: ShadingPattern, color: String? = null, fill: String? = null) {
        shadingValue = Shading(pattern = pattern, color = color, fill = fill)
    }

    /**
     * Language settings for this run. Any argument left `null` omits
     * the matching `<w:lang>` attribute.
     */
    public fun language(
        value: String? = null,
        eastAsia: String? = null,
        bidirectional: String? = null,
    ) {
        languageValue = Language(
            value = value, eastAsia = eastAsia, bidirectional = bidirectional,
        )
    }

    /** Append a soft line break — `<w:br/>` — to the current run. */
    public fun lineBreak() {
        extraChildren += Break(BreakType.LINE)
    }

    /**
     * Append an additional text segment to the current run — comes
     * AFTER the run's main `text(...)` value and any earlier appended
     * children. Use to interleave fields with text in a single
     * formatted run, e.g. `text("Page ") { pageNumber(); appendText(" of "); totalPages() }`.
     */
    public fun appendText(value: String) {
        extraChildren += io.docxkt.model.paragraph.run.Text(value)
    }

    /**
     * Append a `<w:ptab>` positional tab — absolute-position tab
     * stop with explicit alignment / relative-to anchor / leader.
     * Used inside formatted runs (typically as the run's first
     * child for column-stop layouts).
     */
    public fun positionalTab(
        alignment: io.docxkt.model.paragraph.run.PositionalTabAlignment,
        relativeTo: io.docxkt.model.paragraph.run.PositionalTabRelativeTo,
        leader: io.docxkt.model.paragraph.run.PositionalTabLeader,
    ) {
        leadingChildren += io.docxkt.model.paragraph.run.PositionalTab(
            alignment = alignment,
            relativeTo = relativeTo,
            leader = leader,
        )
    }

    /**
     * Append a `PAGE` complex-field chain to the current run —
     * matches upstream's `PageNumber.CURRENT` sentinel inside a
     * `TextRun`'s children, keeping the field formatted with the
     * same `<w:rPr>` as the surrounding text.
     */
    public fun pageNumber() {
        appendComplexField("PAGE")
    }

    /**
     * Append a `NUMPAGES` complex-field chain — total page count
     * — to the current run. Upstream's `PageNumber.TOTAL_PAGES`.
     */
    public fun totalPages() {
        appendComplexField("NUMPAGES")
    }

    /**
     * Append a `SECTIONPAGES` complex-field chain — total pages in
     * the current section — to the current run. Upstream's
     * `PageNumber.TOTAL_PAGES_IN_SECTION`.
     */
    public fun totalPagesInSection() {
        appendComplexField("SECTIONPAGES")
    }

    /**
     * Append a `SECTION` complex-field chain — current section
     * number — to the current run. Upstream's
     * `PageNumber.CURRENT_SECTION`.
     */
    public fun currentSection() {
        appendComplexField("SECTION")
    }

    private fun appendComplexField(instruction: String) {
        extraChildren += FieldChar(FieldCharType.BEGIN)
        extraChildren += InstrText(instruction)
        extraChildren += FieldChar(FieldCharType.SEPARATE)
        extraChildren += FieldChar(FieldCharType.END)
    }

    /** Append a hard page break — `<w:br w:type="page"/>` — to the current run. */
    public fun pageBreak() {
        extraChildren += Break(BreakType.PAGE)
    }

    /**
     * Nest a `<w:footnoteReference w:id="N"/>` reference run
     * inside this run. Upstream's `TextRun({ children: [...,
     * new FootnoteReferenceRun(1)] })` shape — the reference
     * run sits as a child `<w:r>` of the surrounding formatted
     * run, sharing none of its rPr but coexisting in the same
     * outer run element.
     */
    public fun footnoteReference(id: Int) {
        extraChildren += io.docxkt.model.footnote.FootnoteReferenceRun(id = id)
    }

    /** Endnote-variant of [footnoteReference]. */
    public fun endnoteReference(id: Int) {
        extraChildren += io.docxkt.model.footnote.EndnoteReferenceRun(id = id)
    }

    /**
     * Insert a `<w:tab/>` BEFORE this run's text. Matches upstream's
     * `new TextRun({ children: [new Tab(), "..."] })` pattern. Multiple
     * `tab()` calls stack in source order.
     *
     * Use this — not `ParagraphScope.tab()` — when the tab needs to
     * carry the same run-level formatting (bold/italic/etc.) as the
     * adjacent text. The paragraph-level `tab()` produces a separate
     * unformatted run.
     */
    public fun tab() {
        leadingChildren += Tab()
    }

    /**
     * Insert a `<w:br/>` BEFORE this run's text. Matches upstream's
     * `new TextRun({ break: 1, text: "..." })` pattern, where the break
     * comes first inside the formatted run.
     */
    public fun leadingLineBreak() {
        leadingChildren += Break(BreakType.LINE)
    }

    /**
     * Append an inline image to the current run.
     *
     * [widthEmus] / [heightEmus] are dimensions in OOXML English Metric
     * Units (914400 per inch; at 96 DPI, 9525 per pixel). The caller
     * converts — the library does not decode image bytes.
     *
     * The image's relationship id is *deferred*: the call registers the
     * image with [context] and emits a [Drawing] pointing at a fresh
     * `ImageSlot`. `Document.buildDocument()` allocates the real rId
     * and fills the slot before any part serializes.
     */
    public fun image(
        bytes: ByteArray,
        widthEmus: Int,
        heightEmus: Int,
        format: ImageFormat,
        description: String? = null,
    ) {
        val image = Image(
            bytes = bytes,
            widthEmus = widthEmus,
            heightEmus = heightEmus,
            format = format,
            description = description,
        )
        val slot = context.registerImage(image)
        extraChildren += Drawing(
            embedSlot = slot,
            widthEmus = widthEmus,
            heightEmus = heightEmus,
            description = description,
        )
    }

    private var revisionInfo: io.docxkt.model.revision.RevisionInfo? = null
    private var revisionOldProps: RunProperties? = null

    /**
     * `<w:rPrChange>` revision wrapper. Records the "old" run
     * properties before the current properties were applied. The
     * configure block builds the OLD `<w:rPr>` using the same
     * RunScope surface.
     */
    public fun revision(
        id: Int,
        author: String,
        date: String,
        configure: RunScope.() -> Unit = {},
    ) {
        revisionInfo = io.docxkt.model.revision.RevisionInfo(id, author, date)
        val oldScope = RunScope(context).apply(configure)
        revisionOldProps = oldScope.buildProperties()
    }

    internal fun buildProperties(): RunProperties = RunProperties(
        styleReference = styleReference,
        bold = bold,
        italics = italics,
        strike = strike,
        doubleStrike = doubleStrike,
        smallCaps = smallCaps,
        allCaps = allCaps,
        emboss = emboss,
        imprint = imprint,
        noProof = noProof,
        snapToGrid = snapToGrid,
        vanish = vanish,
        superScript = superScript,
        subScript = subScript,
        underline = underlineValue,
        color = color,
        characterSpacing = characterSpacing,
        scale = scale,
        kern = kern,
        position = position,
        size = size,
        font = fontValue,
        highlight = highlight,
        textEffect = textEffect,
        border = border,
        shading = shadingValue,
        rightToLeft = rightToLeft,
        emphasisMark = emphasisMark,
        language = languageValue,
        revision = revisionInfo,
        revisionOldProps = revisionOldProps,
    )

    internal fun extraChildren(): List<XmlComponent> = extraChildren.toList()

    internal fun leadingChildren(): List<XmlComponent> = leadingChildren.toList()
}
