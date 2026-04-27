// No upstream analogue — DSL scope receivers are a Kotlin idiom;
// dolanmiu constructs its tree via typed options objects instead.
package io.docxkt.dsl

import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.Indentation
import io.docxkt.model.paragraph.LineRule
import io.docxkt.model.paragraph.Paragraph
import io.docxkt.model.paragraph.ParagraphBorders
import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.Spacing
import io.docxkt.model.paragraph.TabStop
import io.docxkt.model.paragraph.TabStops
import io.docxkt.model.paragraph.run.Break
import io.docxkt.model.paragraph.run.BreakType
import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern

/**
 * Builder for a single `<w:p>` (paragraph). Collects runs (text,
 * images, hyperlinks) and paragraph-level formatting.
 *
 * ```kotlin
 * paragraph {
 *     alignment = AlignmentType.CENTER
 *     spacing(after = 240, before = 0)
 *     text("Title") { bold = true; size = 32 }
 * }
 * ```
 *
 * Paragraph-level setters (`alignment`, `indent()`, `spacing()`,
 * `keepNext`, ...) may be set in any order relative to `text(...)`
 * calls — output order is driven by `ParagraphProperties`, not DSL
 * call order. Single-arg setters (`alignment`, `keepNext`,
 * `pageBreakBefore` ...) are bare `var`s; multi-arg or compound
 * setters (`indent`, `spacing`, `borders { … }`) are functions.
 */
@DocxktDsl
public class ParagraphScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    // Paragraph children — runs and non-run inline elements like
    // <w:hyperlink>. The loose typing mirrors Paragraph.children.
    private val runs = mutableListOf<io.docxkt.xml.XmlComponent>()

    public var alignment: AlignmentType? = null
    public var keepNext: Boolean? = null
    public var keepLines: Boolean? = null
    public var pageBreakBefore: Boolean? = null
    public var widowControl: Boolean? = null

    /**
     * Paragraph-style reference (`<w:pStyle w:val="StyleId"/>`).
     * Emission-only — style *resolution* against a styles part is
     * not done at the DSL layer.
     */
    public var styleReference: String? = null

    public var bidirectional: Boolean? = null
    public var contextualSpacing: Boolean? = null
    public var suppressLineNumbers: Boolean? = null

    /**
     * Paragraph outline level (`<w:outlineLvl w:val="N"/>`). `0` is a
     * top-level heading, `9` is body text. OOXML's valid range is
     * 0-9; the library does not clamp — the caller is trusted.
     */
    public var outlineLevel: Int? = null

    /**
     * Word-wrap preservation (`<w:wordWrap w:val="0"/>` when true).
     * Upstream's quirk: `wordWrap: true` emits with val=0 (= "do not
     * break long Latin words"). False / null suppresses the element.
     */
    public var wordWrap: Boolean? = null

    private var runDefaultsValue: io.docxkt.model.paragraph.run.RunProperties? = null

    /**
     * Paragraph-level run defaults — emitted as `<w:rPr>` inside
     * `<w:pPr>`. Runs that don't override these properties inherit
     * them. Equivalent to upstream's `new Paragraph({ run: { … } })`.
     *
     * Configure by passing a [RunScope] lambda; only the configured
     * properties become defaults. Empty configuration suppresses the
     * `<w:rPr>` block.
     */
    public fun runDefaults(configure: RunScope.() -> Unit) {
        val scope = RunScope(context).apply(configure)
        runDefaultsValue = scope.buildProperties()
    }

    private var indentationValue: Indentation? = null
    private var spacingValue: Spacing? = null
    private var bordersValue: ParagraphBorders? = null
    private var shadingValue: Shading? = null
    private var tabStopsValue: TabStops? = null
    private var numberingValue: io.docxkt.model.numbering.NumberingReference? = null
    private var framePrValue: io.docxkt.model.paragraph.FrameProperties? = null

    /**
     * Configure `<w:framePr>` — paragraph-level frame positioning.
     *
     * `framePr { widthTwips = …; heightTwips = …; positionXy(...) }`
     * or `positionAligned(...)`. The framePr emits between
     * `<w:pageBreakBefore>` and `<w:widowControl>` inside `<w:pPr>`.
     */
    public fun framePr(configure: FramePrScope.() -> Unit) {
        framePrValue = FramePrScope().apply(configure).build()
    }

    /**
     * Tag this paragraph as a list item — it references an
     * already-registered list template by name and a level index.
     *
     * The `reference` string must match a
     * `DocumentScope.listTemplate(reference) { ... }` call made on
     * the enclosing document. Resolution happens at
     * `Document.buildDocument()` time; referencing an unregistered
     * template throws an `IllegalStateException` there.
     */
    public fun numbering(reference: String, level: Int, instance: Int = 0) {
        val slot = context.registerNumberingReference(
            reference = reference,
            level = level,
            instance = instance,
            inHeaderFooter = context.inHeaderFooterScope,
        )
        numberingValue = io.docxkt.model.numbering.NumberingReference(slot = slot)
    }

    /**
     * Shortcut for the upstream `bullet: { level: N }` idiom —
     * references the phantom default-bullet template (numId=1,
     * the always-emitted abstractNumId=1). Equivalent to
     * `<w:numPr><w:ilvl w:val="N"/><w:numId w:val="1"/></w:numPr>`
     * inline in the paragraph properties without going through
     * the listTemplate registry.
     */
    public fun bullet(level: Int) {
        // Build a slot whose resolvedNumId is hardcoded to 1
        // (DefaultBulletNumbering.NUM_ID).
        val slot = io.docxkt.model.numbering.NumberingReferenceSlot(
            reference = "",
            level = level,
            instance = 0,
            inHeaderFooter = context.inHeaderFooterScope,
        )
        slot.resolvedNumId = io.docxkt.model.numbering.DefaultBulletNumbering.NUM_ID
        numberingValue = io.docxkt.model.numbering.NumberingReference(slot = slot)
    }

    /**
     * Configure `<w:pBdr>`. Same block receiver as
     * [TableScope.borders] — set `top`, `bottom`, `left`, `right`, or
     * `between` individually. A block with no sides set emits nothing
     * (`<w:pBdr>` is suppressed via `IgnoreIfEmptyXmlComponent`).
     *
     * The idiomatic markdown-`ThematicBreak` shape is
     * `borders { bottom = BorderSide(style = BorderStyle.SINGLE, size = 6, color = "auto", space = 1) }`.
     */
    public fun borders(configure: BorderSidesScope.() -> Unit) {
        val scope = BorderSidesScope().apply(configure)
        val anySide = scope.top != null || scope.bottom != null ||
            scope.left != null || scope.right != null || scope.between != null
        bordersValue = if (anySide) {
            ParagraphBorders(
                top = scope.top, bottom = scope.bottom,
                left = scope.left, right = scope.right, between = scope.between,
            )
        } else {
            null
        }
    }

    /** Paragraph-level shading (`<w:shd>`) — same wire shape as table / run. */
    public fun shading(value: Shading) {
        shadingValue = value
    }

    /**
     * Paragraph-level shading from pattern + colors. For the common
     * solid-fill paragraph, `shading(pattern = CLEAR, fill = "EEEEEE")`.
     */
    public fun shading(pattern: ShadingPattern, color: String? = null, fill: String? = null) {
        shadingValue = Shading(pattern = pattern, color = color, fill = fill)
    }

    /**
     * Declare tab stops for this paragraph. Empty vararg leaves
     * `<w:tabs>` un-emitted; each call replaces the previous list
     * (not an append).
     */
    public fun tabs(vararg stops: TabStop) {
        tabStopsValue = if (stops.isNotEmpty()) TabStops(stops.toList()) else null
    }

    /**
     * Set paragraph indentation. Values are OOXML **twips** (1440 twips
     * = 1 inch). Any argument left at its default `null` is omitted
     * from `<w:ind>`.
     */
    public fun indent(
        start: Int? = null,
        end: Int? = null,
        left: Int? = null,
        right: Int? = null,
        hanging: Int? = null,
        firstLine: Int? = null,
    ) {
        indentationValue = Indentation(
            start = start, end = end, left = left, right = right,
            hanging = hanging, firstLine = firstLine,
        )
    }

    /**
     * Set paragraph spacing. Before / after / line are twips;
     * `lineRule` controls how `line` is interpreted (`auto` is Word's
     * implicit default when omitted).
     */
    public fun spacing(
        before: Int? = null,
        after: Int? = null,
        line: Int? = null,
        lineRule: LineRule? = null,
        beforeAutoSpacing: Boolean? = null,
        afterAutoSpacing: Boolean? = null,
    ) {
        spacingValue = Spacing(
            before = before, after = after, line = line, lineRule = lineRule,
            beforeAutoSpacing = beforeAutoSpacing, afterAutoSpacing = afterAutoSpacing,
        )
    }

    /** Add a plain-text run containing [value]. */
    public fun text(value: String) {
        runs += Run(children = listOf(Text(value)))
    }

    /**
     * Add a text run and apply run-level formatting via [configure]. If
     * [configure] touches no properties, the emitted `<w:rPr>` is
     * suppressed — identical output to the no-lambda overload.
     *
     * Break-producing methods inside [configure] (`lineBreak()`,
     * `pageBreak()`) append `<w:br>` children to the same run, after
     * the text.
     */
    public fun text(value: String, configure: RunScope.() -> Unit) {
        val scope = RunScope(context)
        scope.configure()
        // Skip the central <w:t> when the value is empty — matches
        // upstream's `Run.constructor`, which only pushes `new Text(...)`
        // when `options.text !== undefined`. Lets `text("") { image(...) }`
        // emit a clean image-only run (no orphan <w:t/>).
        val core: List<io.docxkt.xml.XmlComponent> =
            if (value.isEmpty()) emptyList() else listOf(Text(value))
        runs += Run(
            children = scope.leadingChildren() + core + scope.extraChildren(),
            properties = scope.buildProperties(),
        )
    }

    /**
     * Emit an opening `<w:bookmarkStart>` marker in this paragraph.
     * Pairs with a later [bookmarkEnd] carrying the same [name].
     *
     * For the common "bookmark one region inside a paragraph" case,
     * prefer [bookmark] — it pairs automatically.
     */
    public fun bookmarkStart(name: String) {
        val id = context.registerBookmark(name)
        runs += io.docxkt.model.bookmark.BookmarkStart(id = id, name = name)
    }

    /**
     * Emit a closing `<w:bookmarkEnd>` marker in this paragraph,
     * referencing the id allocated by a prior [bookmarkStart] with
     * the same [name]. Errors if no such bookmark has been
     * registered.
     */
    public fun bookmarkEnd(name: String) {
        val id = context.findBookmarkId(name)
            ?: error("bookmarkEnd('$name') called with no matching bookmarkStart")
        runs += io.docxkt.model.bookmark.BookmarkEnd(id = id)
    }

    /**
     * Mark a region inside this paragraph as a bookmark. Emits
     * `<w:bookmarkStart>` before [configure]'s runs and
     * `<w:bookmarkEnd>` after.
     */
    public fun bookmark(name: String, configure: ParagraphScope.() -> Unit) {
        val id = context.registerBookmark(name)
        runs += io.docxkt.model.bookmark.BookmarkStart(id = id, name = name)
        configure()
        runs += io.docxkt.model.bookmark.BookmarkEnd(id = id)
    }

    /**
     * Emit a `<w:fldSimple>` as a paragraph child. When [cached]
     * is non-null, wraps a single `<w:r><w:t>` run with the
     * cached display text; otherwise self-closed.
     *
     * [instruction] carries the OOXML field code verbatim (e.g.
     * `"DATE \\@ \"MMM d, yyyy\""`, `"AUTHOR"`, `"NUMPAGES"`).
     * Quotes and special characters are escaped in the
     * `w:instr` attribute automatically.
     */
    public fun fieldSimple(instruction: String, cached: String? = null) {
        runs += io.docxkt.model.field.SimpleField(instruction, cached)
    }

    /**
     * Emit a complex-form field as a single `<w:r>` containing
     * the begin/instrText/separate/(cached)/end chain. Mirrors
     * upstream's `PageNumber.CURRENT` expansion.
     *
     * [dirty] = true emits `w:dirty="true"` on the begin marker —
     * tells Word to recalculate the field on open. Use for
     * SEQ / dynamic fields whose value depends on document state.
     */
    public fun fieldComplex(
        instruction: String,
        cached: String? = null,
        dirty: Boolean = false,
    ) {
        runs += io.docxkt.model.field.ComplexField(instruction, cached, dirty = dirty)
    }

    /**
     * Emit a `SEQ <name>` complex field — sequential identifier /
     * caption counter. Always emits with `w:dirty="true"` so Word
     * recomputes the value at open time.
     */
    public fun sequentialIdentifier(name: String) {
        runs += io.docxkt.model.field.ComplexField("SEQ $name", dirty = true)
    }

    /**
     * Emit a `PAGEREF <bookmark>` field — renders as the page
     * number where the named bookmark sits. Two-marker form
     * (no separate / no cached text), `w:dirty="true"` so Word
     * recomputes at open. Used in TOC / cross-reference flows.
     */
    public fun pageReference(bookmarkName: String) {
        runs += io.docxkt.model.field.PageReference(bookmarkName = bookmarkName)
    }

    /**
     * Append a body-side footnote reference pointing at the
     * footnote with numeric [id] registered via
     * `DocumentScope.footnote(id) { … }`. Emits a run styled
     * with the `FootnoteReference` character style and wrapping
     * `<w:footnoteReference w:id="id"/>`.
     */
    public fun footnoteReference(id: Int) {
        runs += io.docxkt.model.footnote.FootnoteReferenceRun(id = id)
    }

    /** Same, for endnotes. */
    public fun endnoteReference(id: Int) {
        runs += io.docxkt.model.footnote.EndnoteReferenceRun(id = id)
    }

    /**
     * Wrap the runs configured inside [configure] in a
     * `<w:ins w:id w:author w:date>` revision marker.
     *
     * [id] is auto-allocated via
     * [DocumentContext.nextRevisionId] when null. Provide
     * explicitly for reproducible fixtures.
     */
    public fun insertedText(
        author: String,
        date: String,
        id: Int? = null,
        configure: RevisionRunsScope.() -> Unit,
    ) {
        val scope = RevisionRunsScope(context)
        scope.configure()
        val allocated = id ?: context.nextRevisionId()
        runs += io.docxkt.model.revision.InsertedRun(
            id = allocated,
            author = author,
            date = date,
            runs = scope.buildRuns(),
        )
    }

    /**
     * Wrap the runs configured inside [configure] in a
     * `<w:del>` revision marker. Each `<w:t>` child gets
     * rewritten to `<w:delText>` at emit time.
     */
    public fun deletedText(
        author: String,
        date: String,
        id: Int? = null,
        configure: RevisionRunsScope.() -> Unit,
    ) {
        val scope = RevisionRunsScope(context)
        scope.configure()
        val allocated = id ?: context.nextRevisionId()
        runs += io.docxkt.model.revision.DeletedRun(
            id = allocated,
            author = author,
            date = date,
            runs = scope.buildRuns(),
        )
    }

    /**
     * Insert a symbol glyph from a symbol font. Emits a run
     * containing just `<w:sym w:char="…" w:font="…"/>`.
     *
     * [char] is a short hex string (e.g. `"F0FC"`); [font]
     * defaults to `"Wingdings"` matching upstream's default.
     */
    public fun symbol(char: String, font: String = "Wingdings") {
        runs += io.docxkt.model.paragraph.run.Run(
            children = listOf(
                io.docxkt.model.symbol.Symbol(char = char, font = font),
            ),
        )
    }

    /**
     * Emit a `<w:commentRangeStart w:id="…"/>` marker opening
     * a commented range. Pair with a later [commentRangeEnd]
     * using the same numeric id.
     */
    public fun commentRangeStart(id: Int) {
        runs += io.docxkt.model.comment.CommentRangeStart(id = id)
    }

    /** Emit a `<w:commentRangeEnd w:id="…"/>` marker. */
    public fun commentRangeEnd(id: Int) {
        runs += io.docxkt.model.comment.CommentRangeEnd(id = id)
    }

    /**
     * Emit a `<w:r><w:commentReference w:id="…"/></w:r>` run —
     * the visible anchor that Word/LibreOffice renders as a
     * small superscript marker. Typically placed right after
     * [commentRangeEnd].
     */
    public fun commentReference(id: Int) {
        runs += io.docxkt.model.comment.CommentReferenceRun(id = id)
    }

    /**
     * Same as [commentReference] but the marker run carries the
     * run-level formatting configured in [configure]. Matches
     * upstream's `new TextRun({ children: [new CommentReference(id)],
     * bold: true })` pattern (demo-73).
     */
    public fun commentReference(id: Int, configure: RunScope.() -> Unit) {
        val scope = RunScope(context).apply(configure)
        runs += io.docxkt.model.comment.CommentReferenceRun(
            id = id,
            properties = scope.buildProperties(),
        )
    }

    /**
     * Insert a `PAGE` field — renders as the current page number
     * at view time. Thin wrapper over [fieldComplex]. Matches
     * upstream's `PageNumber.CURRENT` sentinel expansion.
     */
    public fun pageNumber() {
        runs += io.docxkt.model.field.ComplexField("PAGE")
    }

    /**
     * Insert a `NUMPAGES` field — renders as the total page count
     * in the document.
     */
    public fun totalPages() {
        runs += io.docxkt.model.field.ComplexField("NUMPAGES")
    }

    /**
     * Insert a `SECTIONPAGES` field — renders as the total page
     * count within the current section.
     */
    public fun totalPagesInSection() {
        runs += io.docxkt.model.field.ComplexField("SECTIONPAGES")
    }

    /**
     * Insert a `SECTION` field — renders as the current section
     * number.
     */
    public fun currentSection() {
        runs += io.docxkt.model.field.ComplexField("SECTION")
    }

    /**
     * Wrap a group of runs in a `<w:hyperlink>` pointing at the
     * external URL [url]. Runs configured inside [configure] are
     * appended inside the hyperlink; they can carry their own
     * `styleReference` / formatting.
     *
     * The relationship id is allocated at `Document.buildDocument()`
     * time — each `hyperlink(...)` call site gets its own rId, even
     * when two calls target the same URL.
     *
     * External-URL form. For internal (bookmark-anchor) hyperlinks
     * use [internalHyperlink].
     */
    public fun hyperlink(url: String, configure: HyperlinkScope.() -> Unit) {
        val scope = HyperlinkScope(context)
        scope.configure()
        val slot = context.registerHyperlink(url)
        runs += io.docxkt.model.hyperlink.Hyperlink.external(
            slot = slot,
            runs = scope.buildRuns(),
        )
    }

    /**
     * Wrap a group of runs in a `<w:hyperlink w:anchor="…">`
     * pointing at a bookmark defined elsewhere in the same
     * document. No relationship allocated — internal hyperlinks
     * resolve by name inside Word.
     */
    public fun internalHyperlink(anchor: String, configure: HyperlinkScope.() -> Unit) {
        val scope = HyperlinkScope(context)
        scope.configure()
        runs += io.docxkt.model.hyperlink.Hyperlink.internal(
            anchor = anchor,
            runs = scope.buildRuns(),
        )
    }

    /**
     * Append a standalone image run. Convenience for
     * `text("") { image(...) }` without the empty text.
     */
    public fun image(
        bytes: ByteArray,
        widthEmus: Int,
        heightEmus: Int,
        format: io.docxkt.model.drawing.ImageFormat,
        description: String? = null,
    ) {
        val scope = RunScope(context)
        scope.image(bytes, widthEmus, heightEmus, format, description)
        runs += Run(
            children = scope.extraChildren(),
            properties = scope.buildProperties(),
        )
    }

    /**
     * Append an inline math expression — `<m:oMath>` wrapper
     * containing the components built inside [configure].
     * Math sits as a paragraph child alongside runs and
     * other inline elements.
     */
    public fun math(configure: MathScope.() -> Unit) {
        val scope = MathScope().apply(configure)
        runs += io.docxkt.model.math.OMath(children = scope.build())
    }

    /**
     * Append a checkbox content control — `<w:sdt>` containing
     * a `<w14:checkbox>` form control plus a rendered
     * `<w:sym>` matching the current state.
     *
     * Defaults match upstream:
     * - checkedState: `"2612"` ☒ in `MS Gothic`
     * - uncheckedState: `"2610"` ☐ in `MS Gothic`
     *
     * Override [checkedState] / [uncheckedState] for custom
     * symbols (e.g. Wingdings glyphs).
     */
    public fun checkBox(
        checked: Boolean = false,
        alias: String? = null,
        checkedState: io.docxkt.model.formcontrol.CheckBoxState =
            io.docxkt.model.formcontrol.CheckBoxState.DEFAULT_CHECKED,
        uncheckedState: io.docxkt.model.formcontrol.CheckBoxState =
            io.docxkt.model.formcontrol.CheckBoxState.DEFAULT_UNCHECKED,
    ) {
        runs += io.docxkt.model.formcontrol.CheckBox(
            checked = checked,
            alias = alias,
            checkedState = checkedState,
            uncheckedState = uncheckedState,
        )
    }

    /**
     * Append a textbox — a sized rectangular text container
     * (modern DrawingML `<wps:wsp>`) with paragraphs inside
     * `<w:txbxContent>`. Inline form only — floating textboxes
     * (anchor wrapper) are deferred.
     *
     * Inside [configure], `paragraph { ... }` adds paragraphs
     * to the textbox body using the same surface as
     * body-level paragraphs.
     */
    public fun textbox(
        widthEmus: Int,
        heightEmus: Int,
        description: String? = null,
        configure: TextboxScope.() -> Unit,
    ) {
        val scope = TextboxScope(context).apply(configure)
        runs += io.docxkt.model.paragraph.run.Run(
            children = listOf(
                io.docxkt.model.textbox.Textbox(
                    widthEmus = widthEmus,
                    heightEmus = heightEmus,
                    paragraphs = scope.buildParagraphs(),
                    bodyMargins = scope.bodyMargins,
                    verticalAnchor = scope.verticalAnchor,
                    description = description,
                ),
            ),
        )
    }

    /**
     * Append a floating (anchor) image. The image is registered
     * with the same allocator path as inline images; the
     * difference is the `<wp:anchor>` wrapper instead of
     * `<wp:inline>` and the positioning + wrap configuration
     * supplied via [configure].
     *
     * `positionH(...)` and `positionV(...)` are required inside
     * the configure block — the wire has no sensible default.
     */
    public fun imageAnchor(
        bytes: ByteArray,
        widthEmus: Int,
        heightEmus: Int,
        format: io.docxkt.model.drawing.ImageFormat,
        description: String? = null,
        configure: AnchorScope.() -> Unit,
    ) {
        val scope = AnchorScope().apply(configure)
        val image = io.docxkt.model.drawing.Image(
            bytes = bytes,
            widthEmus = widthEmus,
            heightEmus = heightEmus,
            format = format,
            description = description,
        )
        val slot = context.registerImage(image)
        val anchor = io.docxkt.model.drawing.AnchorDrawing(
            embedSlot = slot,
            widthEmus = widthEmus,
            heightEmus = heightEmus,
            horizontalPosition = scope.resolvedHorizontal(),
            verticalPosition = scope.resolvedVertical(),
            wrap = scope.wrap,
            behindDoc = scope.behindDoc,
            allowOverlap = scope.allowOverlap,
            layoutInCell = scope.layoutInCell,
            lockAnchor = scope.lockAnchor,
            anchorMargins = scope.anchorMargins,
            relativeHeight = scope.relativeHeight,
            description = description,
        )
        runs += Run(children = listOf(anchor))
    }

    /**
     * Emit a standalone run containing a single soft line break:
     * `<w:r><w:br/></w:r>`. Use this when the break sits between
     * runs rather than at the tail of one.
     */
    public fun lineBreak() {
        runs += Run(children = listOf(Break(BreakType.LINE)))
    }

    /**
     * Emit a standalone run containing a single hard page break:
     * `<w:r><w:br w:type="page"/></w:r>`.
     */
    public fun pageBreak() {
        runs += Run(children = listOf(Break(BreakType.PAGE)))
    }

    /**
     * Emit a standalone run with a column break: `<w:r><w:br
     * w:type="column"/></w:r>`. Meaningful only inside a
     * `<w:cols>`-configured section; without columns, Word
     * may render it as a page break.
     */
    public fun columnBreak() {
        runs += Run(children = listOf(Break(BreakType.COLUMN)))
    }

    /**
     * Emit a standalone run containing a tab character:
     * `<w:r><w:tab/></w:r>`. The tab advances to the next
     * defined `<w:tabs>` stop in the paragraph (or Word's
     * default 720-twip grid if none defined).
     */
    public fun tab() {
        runs += Run(children = listOf(io.docxkt.model.paragraph.run.Tab()))
    }

    /**
     * Emit a standalone run with a soft hyphen: `<w:r>
     * <w:softHyphen/></w:r>`. Renders as nothing unless the
     * surrounding word needs to wrap.
     */
    public fun softHyphen() {
        runs += Run(children = listOf(io.docxkt.model.paragraph.run.SoftHyphen()))
    }

    /**
     * Emit a standalone run with a no-break hyphen:
     * `<w:r><w:noBreakHyphen/></w:r>`. Visible hyphen that
     * prevents wrapping at that position.
     */
    public fun noBreakHyphen() {
        runs += Run(children = listOf(io.docxkt.model.paragraph.run.NoBreakHyphen()))
    }

    internal fun build(): Paragraph {
        return Paragraph(children = runs.toList(), properties = buildProperties())
    }

    /**
     * Extract the paragraph-level properties only — no runs. Used by
     * [StyleScope] when the DSL caller configures a `paragraph { }`
     * block inside a `paragraphStyle(id) { }` registration.
     */
    internal fun buildProperties(): ParagraphProperties? {
        val hasAnyProp = styleReference != null || alignment != null ||
            keepNext != null || keepLines != null ||
            pageBreakBefore != null || widowControl != null ||
            numberingValue != null ||
            bordersValue != null || shadingValue != null || tabStopsValue != null ||
            bidirectional != null || contextualSpacing != null ||
            outlineLevel != null || suppressLineNumbers != null ||
            framePrValue != null || wordWrap == true ||
            runDefaultsValue != null ||
            (indentationValue != null && !indentationValue!!.isEmpty()) ||
            (spacingValue != null && !spacingValue!!.isEmpty())
        if (!hasAnyProp) return null
        // Upstream auto-promotes a paragraph with numbering (and no
        // explicit style) to the built-in `ListParagraph` style —
        // Word/LibreOffice expect it. Match byte-for-byte.
        val effectiveStyleRef = when {
            styleReference != null -> styleReference
            numberingValue != null -> "ListParagraph"
            else -> null
        }
        return ParagraphProperties(
            styleReference = effectiveStyleRef,
            keepNext = keepNext,
            keepLines = keepLines,
            pageBreakBefore = pageBreakBefore,
            framePr = framePrValue,
            widowControl = widowControl,
            numbering = numberingValue,
            borders = bordersValue,
            shading = shadingValue,
            wordWrap = wordWrap,
            tabStops = tabStopsValue,
            bidirectional = bidirectional,
            spacing = spacingValue,
            indentation = indentationValue,
            contextualSpacing = contextualSpacing,
            alignment = alignment,
            outlineLevel = outlineLevel,
            suppressLineNumbers = suppressLineNumbers,
            runDefaults = runDefaultsValue,
        )
    }
}
