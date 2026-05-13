// No upstream analogue — DSL scope receivers are a Kotlin idiom;
// dolanmiu constructs its tree via typed options objects instead.
package io.docxkt.dsl

import io.docxkt.model.Body
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.model.section.PageBorderDisplay
import io.docxkt.model.section.PageBorderOffsetFrom
import io.docxkt.model.section.PageBorderZOrder
import io.docxkt.model.section.PageBorders
import io.docxkt.model.section.PageMargins
import io.docxkt.model.section.PageOrientation
import io.docxkt.model.section.PageSize
import io.docxkt.model.section.SectionProperties
import io.docxkt.model.paragraph.Indentation
import io.docxkt.model.paragraph.LineRule
import io.docxkt.model.paragraph.Paragraph
import io.docxkt.model.paragraph.ParagraphBorders
import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.Spacing
import io.docxkt.model.paragraph.TabStop
import io.docxkt.model.paragraph.TabStops
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.model.paragraph.run.Break
import io.docxkt.model.paragraph.run.BreakType
import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text

/**
 * Builder for the `<w:body>` under a [document][io.docxkt.api.document]
 * block. Accumulates block-level content (paragraphs and tables) in
 * source order until `build()` is called internally.
 */
@DocxktDsl
public class DocumentScope internal constructor() {
    private val blocks = mutableListOf<io.docxkt.xml.XmlComponent>()
    private var pageSizeValue: PageSize = PageSize.a4(PageOrientation.PORTRAIT)
    private var pageMarginsValue: PageMargins = PageMargins()
    private var pageBordersValue: PageBorders? = null
    private var lineNumberingValue: io.docxkt.model.section.LineNumbering? = null
    private var columnsValue: io.docxkt.model.section.Columns? = null
    private var sectionTypeValue: io.docxkt.model.section.SectionType? = null
    private var sectionVerticalAlignValue: io.docxkt.model.section.SectionVerticalAlign? = null
    private var pageNumbersStartValue: Int? = null
    private var pageNumbersFormatValue: io.docxkt.model.section.PageNumberFormat? = null
    private var pageNumbersSeparatorValue: io.docxkt.model.section.PageNumberSeparator? = null
    private var pageNumbersChapStyleValue: Int? = null
    internal val context: DocumentContext = DocumentContext()

    /**
     * Configure `<w:pgNumType>` attributes on the document-level
     * (trailing) section.
     */
    public fun pageNumbers(
        start: Int? = null,
        format: io.docxkt.model.section.PageNumberFormat? = null,
        separator: io.docxkt.model.section.PageNumberSeparator? = null,
        chapStyle: Int? = null,
    ) {
        pageNumbersStartValue = start
        pageNumbersFormatValue = format
        pageNumbersSeparatorValue = separator
        pageNumbersChapStyleValue = chapStyle
    }

    /**
     * Set the document-level (trailing) section's `<w:type>` value
     * — controls how that final section begins relative to the
     * previous one. Null = inherit (`nextPage`).
     */
    public fun sectionType(value: io.docxkt.model.section.SectionType?) {
        sectionTypeValue = value
    }

    /**
     * Set the document-level (trailing) section's `<w:vAlign>` —
     * vertical positioning of section content within the page area.
     * Null = inherit (`top`).
     */
    public fun sectionVerticalAlign(value: io.docxkt.model.section.SectionVerticalAlign?) {
        sectionVerticalAlignValue = value
    }

    /** Add a paragraph; apply [configure] to its scope. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        blocks += scope.build()
    }

    /** Add a table; apply [configure] to its scope. */
    public fun table(configure: TableScope.() -> Unit) {
        val scope = TableScope(context)
        scope.configure()
        blocks += scope.build()
    }

    /**
     * Add a table-of-contents block (`<w:sdt>` wrapping a
     * TOC complex-field chain). Minimal form — no cached entries.
     */
    public fun tableOfContents(configure: TocScope.() -> Unit = {}) {
        val scope = TocScope()
        scope.configure()
        blocks += scope.build()
    }

    /**
     * Configure document metadata (title / creator / timestamps /
     * custom key-value pairs) that lands in `docProps/core.xml`
     * and `docProps/custom.xml`. Optional — when omitted, core/app
     * still emit with upstream defaults.
     */
    public fun properties(configure: PropertiesScope.() -> Unit) {
        val scope = PropertiesScope(context)
        scope.configure()
        scope.build()
    }

    /**
     * Configure document-level settings (`word/settings.xml`) —
     * compatibility version, tab stop, track revisions, etc.
     * Optional — settings.xml always emits, even with defaults.
     */
    public fun settings(configure: SettingsScope.() -> Unit) {
        val scope = SettingsScope(context)
        scope.configure()
        scope.build()
    }

    /**
     * Register a list template. `reference` is the name paragraphs
     * use in `numbering(reference, level)`. Calling twice with the
     * same reference replaces the earlier template.
     *
     * Emits one `<w:abstractNum>` + one `<w:num>` in the document's
     * `word/numbering.xml` part at assembly time. numIds and
     * abstractNumIds are allocated at [buildDocument] time.
     */
    public fun listTemplate(reference: String, configure: ListTemplateScope.() -> Unit) {
        val scope = ListTemplateScope()
        scope.configure()
        context.registerListTemplate(reference, scope.build())
    }

    /**
     * Embed a custom font binary in the document. [name] matches what
     * `RunScope.font(name)` references. [bytes] is the raw TTF; the
     * library obfuscates per ECMA-376 Part 2 §11.1 and stores under
     * `word/fonts/<name>.odttf`. Call multiple times for multiple
     * fonts; calling twice with the same name replaces.
     *
     * Bold / italic / bold-italic embeds are not exposed — upstream's
     * public DSL only emits `<w:embedRegular>`.
     */
    public fun embedFont(
        name: String,
        bytes: ByteArray,
        characterSet: io.docxkt.model.font.FontCharacterSet =
            io.docxkt.model.font.FontCharacterSet.ANSI,
    ) {
        context.registerEmbeddedFont(
            io.docxkt.model.font.EmbeddedFont(
                name = name,
                bytes = bytes,
                characterSet = characterSet,
            ),
        )
    }

    /**
     * Register a user footnote under [id]. The footnote
     * paragraphs appear inside `<w:footnote w:id="id">` in
     * `word/footnotes.xml`. Body references use
     * [ParagraphScope.footnoteReference].
     */
    public fun footnote(id: Int, configure: FootnoteScope.() -> Unit) {
        val scope = FootnoteScope(context)
        scope.configure()
        context.registerFootnote(scope.build(id))
    }

    /** Register a user endnote — same shape as [footnote]. */
    public fun endnote(id: Int, configure: FootnoteScope.() -> Unit) {
        val scope = FootnoteScope(context)
        scope.configure()
        context.registerEndnote(scope.build(id))
    }

    /**
     * Register a comment under numeric [id]. Body paragraphs
     * configured via [configure] appear inside
     * `<w:comment w:id>` in `word/comments.xml`. Body markers
     * (`commentRangeStart` / `commentRangeEnd` /
     * `commentReference`) are inserted via [ParagraphScope].
     *
     * [date] is a W3CDTF string (e.g.
     * `"2026-04-24T00:00:00.000Z"`). Required — OOXML
     * expects every comment to carry a timestamp.
     */
    public fun comment(
        id: Int,
        author: String? = null,
        initials: String? = null,
        date: String,
        configure: CommentScope.() -> Unit,
    ) {
        val scope = CommentScope(context)
        scope.configure()
        context.registerComment(scope.build(id, author, initials, date))
    }

    /**
     * Register a paragraph style. `id` is the identifier paragraphs
     * reference via `styleReference = "id"`. Calling twice with the
     * same id replaces the earlier definition.
     *
     * Emits one `<w:style w:type="paragraph" w:styleId="id">` in the
     * document's `word/styles.xml` part at assembly time.
     */
    public fun paragraphStyle(id: String, configure: StyleScope.() -> Unit) {
        val scope = StyleScope()
        scope.configure()
        context.registerStyle(scope.buildParagraphStyle(id))
    }

    /**
     * Register a character style. `id` is the identifier runs reference
     * via `styleReference = "id"`. Calling twice with the same id
     * replaces the earlier definition. Character styles carry only a
     * `<w:rPr>` — a `paragraph { }` block inside the scope is ignored.
     */
    public fun characterStyle(id: String, configure: StyleScope.() -> Unit) {
        val scope = StyleScope()
        scope.configure()
        context.registerStyle(scope.buildCharacterStyle(id))
    }

    /**
     * Register `<w:docDefaults>`: document-wide default formatting
     * that applies to every paragraph / run unless overridden.
     * Mirrors upstream's `styles.default.{run,paragraph}` options
     * block.
     */
    public fun documentDefaults(configure: DocumentDefaultsScope.() -> Unit) {
        val scope = DocumentDefaultsScope().apply(configure)
        context.documentDefaults = io.docxkt.model.style.DocumentDefaults(
            runDefaults = scope.runProps,
            paragraphDefaults = scope.paragraphProps,
        )
    }

    /**
     * Set the page size explicitly in twips. Use [a4Portrait] /
     * [a4Landscape] for the common A4 presets — those match upstream's
     * exact integers so fixtures stay byte-stable.
     */
    public fun pageSize(
        widthTwips: Int,
        heightTwips: Int,
        orientation: PageOrientation = PageOrientation.PORTRAIT,
    ) {
        pageSizeValue = PageSize(widthTwips, heightTwips, orientation)
    }

    /** A4 portrait — `w=11906 h=16838 orient=portrait`. */
    public fun a4Portrait() {
        pageSizeValue = PageSize.a4(PageOrientation.PORTRAIT)
    }

    /**
     * End the current section. Emits an empty paragraph whose
     * `<w:pPr>` carries a `<w:sectPr>` describing the section that
     * ENDS here — page size / margins / borders / per-section
     * headers / footers. The document's trailing `<w:sectPr>`
     * describes the LAST section.
     *
     * Inside [configure], call `a4Portrait()` / `a4Landscape()` /
     * `pageSize(widthTwips, heightTwips)` / `margins(...)` /
     * `pageBorders { ... }` / `header(type) { ... }` /
     * `footer(type) { ... }` exactly as on [DocumentScope]. The
     * document-level `header { }` / `footer { }` on
     * [DocumentScope] still bind to the trailing section.
     */
    public fun sectionBreak(configure: SectionBreakScope.() -> Unit) {
        val scope = SectionBreakScope(context).apply(configure)
        // Defer SectionProperties construction until buildDocument so
        // each section's headers/footers can be allocated rIds in
        // source order. The resolver substitutes the placeholder
        // before Body.appendXml() runs.
        blocks += io.docxkt.dsl.PendingSectionBreak(scope)
    }

    /**
     * Configure `<w:pgBorders>` on the (default) section. Sides are
     * set via `top { ... } bottom { ... } left { ... } right { ... }`
     * lambdas; top-level knobs `display`, `offsetFrom`, `zOrder` are
     * properties on the scope.
     *
     * Calling `pageBorders { }` with no sides set produces an empty
     * `<w:pgBorders/>` per the configured attributes. Don't call it
     * if you want to opt out — the field starts null.
     */
    public fun pageBorders(configure: PageBordersScope.() -> Unit) {
        val scope = PageBordersScope().apply(configure)
        pageBordersValue = PageBorders(
            top = scope.top,
            left = scope.left,
            bottom = scope.bottom,
            right = scope.right,
            display = scope.display,
            offsetFrom = scope.offsetFrom,
            zOrder = scope.zOrder,
        )
    }

    /**
     * Configure `<w:cols>` for the (default) section — multi-
     * column layout.
     *
     * - [count] is the number of columns.
     * - [equalWidth] = true (the common case) splits the
     *   available width equally; pass [spaceTwips] to set the
     *   gap. When false, [individual] supplies per-column
     *   widths.
     * - [separator] = true draws a vertical line between columns.
     */
    public fun columns(
        count: Int,
        equalWidth: Boolean? = null,
        spaceTwips: Int? = null,
        separator: Boolean? = null,
        individual: List<io.docxkt.model.section.Column> = emptyList(),
    ) {
        columnsValue = io.docxkt.model.section.Columns(
            count = count,
            equalWidth = equalWidth,
            spaceTwips = spaceTwips,
            separator = separator,
            individual = individual,
        )
    }

    /**
     * Configure `<w:lnNumType>` for the (default) section — line
     * numbering. All four parameters are optional; unset
     * attributes fall through to upstream defaults.
     */
    public fun lineNumbering(
        countBy: Int? = null,
        start: Int? = null,
        distanceTwips: Int? = null,
        restart: io.docxkt.model.section.LineNumberRestart? = null,
    ) {
        lineNumberingValue = io.docxkt.model.section.LineNumbering(
            countBy = countBy,
            start = start,
            distance = distanceTwips,
            restart = restart,
        )
    }

    /** A4 landscape — `w=16838 h=11906 orient=landscape`. */
    public fun a4Landscape() {
        pageSizeValue = PageSize.a4(PageOrientation.LANDSCAPE)
    }

    /**
     * Set page margins in twips. Any argument left `null` falls through
     * to the upstream default for that side (1440 edges, 708 header /
     * footer, 0 gutter).
     */
    public fun margins(
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null,
        left: Int? = null,
        header: Int? = null,
        footer: Int? = null,
        gutter: Int? = null,
    ) {
        val d = PageMargins()
        pageMarginsValue = PageMargins(
            top = top ?: d.top,
            right = right ?: d.right,
            bottom = bottom ?: d.bottom,
            left = left ?: d.left,
            header = header ?: d.header,
            footer = footer ?: d.footer,
            gutter = gutter ?: d.gutter,
        )
    }

    internal fun buildDocument(): io.docxkt.api.Document {
        // Allocator order matches upstream's
        // `addDefaultRelationships()` (`file.ts:320-378`):
        // implicit rels (styles, numbering, footnotes, endnotes,
        // settings, comments) occupy rId1-6 (fixed prefix), then
        // user-visible rels (header → footer → image → hyperlink)
        // start at rId7. fontTable goes LAST (after images, before
        // hyperlinks per upstream). Empty implicit slots stay unused.
        val allocator = io.docxkt.part.RelationshipAllocator()

        // Reserve implicit prefix slots — only emitted in rels file
        // when their content is non-null.
        val styles = context.styles()
        val docDefaults = context.documentDefaults
        val stylesRid = if (styles.isNotEmpty() || docDefaults != null) allocator.implicitRid("styles") else null
        val listTemplates = context.listTemplates()
        // numbering.xml is emitted when EITHER the document
        // registered list templates OR any paragraph references
        // numbering. When emitted, the phantom default-bullet
        // abstractNum + concrete are prepended (matches upstream's
        // hardcoded default).
        val needsNumberingPart = listTemplates.isNotEmpty() ||
            context.pendingNumberingReferences().isNotEmpty()
        val numberingRid = if (needsNumberingPart) allocator.implicitRid("numbering") else null
        val userFootnotes = context.footnotes()
        val footnotesRid = if (userFootnotes.isNotEmpty()) allocator.implicitRid("footnotes") else null
        val userEndnotes = context.endnotes()
        val endnotesRid = if (userEndnotes.isNotEmpty()) allocator.implicitRid("endnotes") else null
        val settingsRid = allocator.implicitRid("settings")
        val userComments = context.comments()
        val commentsRid = if (userComments.isNotEmpty()) allocator.implicitRid("comments") else null

        // H/F rId allocation walks all sections in source order.
        // Each section break (mid-body) plus the trailing section
        // gets its own H/F refs. Per section, refs go in canonical
        // default → first → even order. Headers all come before
        // any footers within a section (upstream's
        // `addHeaderFooterGroup` push order).
        val headerEntries = mutableListOf<io.docxkt.api.HeaderEntry>()
        val footerEntries = mutableListOf<io.docxkt.api.FooterEntry>()
        val sectionHeaderRefs = mutableListOf<List<io.docxkt.model.section.HeaderFooterRef>>()
        val sectionFooterRefs = mutableListOf<List<io.docxkt.model.section.HeaderFooterRef>>()

        fun allocateSectionHF(
            headers: Map<HeaderFooterReferenceType, io.docxkt.model.header.Header>,
            footers: Map<HeaderFooterReferenceType, io.docxkt.model.footer.Footer>,
        ) {
            val hRefs = mutableListOf<io.docxkt.model.section.HeaderFooterRef>()
            val fRefs = mutableListOf<io.docxkt.model.section.HeaderFooterRef>()
            // Canonical order: DEFAULT → FIRST → EVEN.
            for (t in listOf(
                HeaderFooterReferenceType.DEFAULT,
                HeaderFooterReferenceType.FIRST,
                HeaderFooterReferenceType.EVEN,
            )) {
                headers[t]?.let { h ->
                    val rid = allocator.nextId()
                    headerEntries += io.docxkt.api.HeaderEntry(header = h, rid = rid)
                    hRefs += io.docxkt.model.section.HeaderFooterRef(type = t, rid = rid)
                }
            }
            for (t in listOf(
                HeaderFooterReferenceType.DEFAULT,
                HeaderFooterReferenceType.FIRST,
                HeaderFooterReferenceType.EVEN,
            )) {
                footers[t]?.let { f ->
                    val rid = allocator.nextId()
                    footerEntries += io.docxkt.api.FooterEntry(footer = f, rid = rid)
                    fRefs += io.docxkt.model.section.HeaderFooterRef(type = t, rid = rid)
                }
            }
            sectionHeaderRefs += hRefs
            sectionFooterRefs += fRefs
        }

        // Walk mid-body section breaks first.
        for (block in blocks) {
            if (block is io.docxkt.dsl.PendingSectionBreak) {
                allocateSectionHF(block.scope.headers, block.scope.footers)
            }
        }
        // Trailing section uses document-level H/F slots.
        val trailingHeaders = buildMap<HeaderFooterReferenceType, io.docxkt.model.header.Header> {
            headerValue?.let { put(HeaderFooterReferenceType.DEFAULT, it) }
            firstHeaderValue?.let { put(HeaderFooterReferenceType.FIRST, it) }
            evenHeaderValue?.let { put(HeaderFooterReferenceType.EVEN, it) }
        }
        val trailingFooters = buildMap<HeaderFooterReferenceType, io.docxkt.model.footer.Footer> {
            footerValue?.let { put(HeaderFooterReferenceType.DEFAULT, it) }
            firstFooterValue?.let { put(HeaderFooterReferenceType.FIRST, it) }
            evenFooterValue?.let { put(HeaderFooterReferenceType.EVEN, it) }
        }
        allocateSectionHF(trailingHeaders, trailingFooters)
        val trailingHeaderRefs = sectionHeaderRefs.last()
        val trailingFooterRefs = sectionFooterRefs.last()

        // Hyperlinks: one rId per call site, in source order.
        val hyperlinkSlots = context.hyperlinks()
        for (slot in hyperlinkSlots) {
            slot.resolvedRid = allocator.nextId()
        }

        // Phantom default-bullet abstractNumId=1 emits unconditionally
        // (matches upstream's hardcoded default-bullet-numbering).
        // User abstracts shift to 2+.
        listTemplates.forEachIndexed { idx, template ->
            template.abstractNumId = idx + 2
            // numId is still tracked on ListTemplate for compatibility
            // — concretes are resolved per-instance below.
            template.numId = idx + 2
        }
        // Concrete numbering: one <w:num> per unique (reference, instance).
        // Upstream's `concreteNumUniqueNumericIdGen` starts at 1++ → 2,3,...
        // (1 is reserved by the phantom default-bullet concrete).
        val pendingRefs = context.pendingNumberingReferences()
        val concreteByKey = mutableMapOf<Pair<String, Int>, Int>()
        // Track which keys have at least one BODY-context ref. Only
        // those get concretes emitted (matches upstream's prepForXml
        // ordering quirk: H/F refs register concretes too late for
        // Numbering serialization).
        val keyHasBodyRef = mutableMapOf<Pair<String, Int>, Boolean>()
        var concreteCounter = 1 // phantom uses 1; first user concrete = 2
        for (slot in pendingRefs) {
            val key = slot.reference to slot.instance
            val numId = concreteByKey.getOrPut(key) {
                concreteCounter += 1
                concreteCounter
            }
            slot.resolvedNumId = numId
            if (!slot.inHeaderFooter) keyHasBodyRef[key] = true
        }
        // Build the concrete-numbering descriptors (passed to
        // NumberingPart). Order: insertion order of unique keys.
        // SKIP keys that only have H/F refs (no body-context).
        val concreteDescriptors = concreteByKey.entries
            .filter { keyHasBodyRef[it.key] == true }
            .map { (key, numId) ->
                val template = context.findListTemplate(key.first)
                    ?: error("numbering(reference = \"${key.first}\") — no listTemplate with that name")
                io.docxkt.api.ConcreteNumberingDescriptor(
                    numId = numId,
                    abstractNumId = template.abstractNumId,
                    startOverride = template.levels.firstOrNull()?.start ?: 1,
                )
            }

        // Partition images by owner part. Body images keep the
        // document-level rId allocator; header/footer images get
        // per-part rIds starting at rId0 (matches upstream).
        //
        // Build reverse lookup HeaderScope/FooterScope -> partIndex
        // so the per-part list can be assembled in headerEntries
        // index order.
        val imageEntries = context.images()
        val headerScopeToIdx = mutableMapOf<HeaderScope, Int>()
        val footerScopeToIdx = mutableMapOf<FooterScope, Int>()
        headerEntries.forEachIndexed { idx, entry ->
            val scope = context.headerScopeOf(entry.header) ?: return@forEachIndexed
            headerScopeToIdx[scope] = idx
        }
        footerEntries.forEachIndexed { idx, entry ->
            val scope = context.footerScopeOf(entry.footer) ?: return@forEachIndexed
            footerScopeToIdx[scope] = idx
        }

        val bodyImageEntries = mutableListOf<DocumentContext.ImageEntry>()
        val headerImageEntriesByIdx = mutableMapOf<Int, MutableList<DocumentContext.ImageEntry>>()
        val footerImageEntriesByIdx = mutableMapOf<Int, MutableList<DocumentContext.ImageEntry>>()
        for (entry in imageEntries) {
            when (val owner = entry.owner) {
                DocumentContext.ImageOwner.Body -> bodyImageEntries += entry
                is DocumentContext.ImageOwner.Header -> {
                    val idx = headerScopeToIdx[owner.scope]
                        ?: error("Image registered against an unallocated HeaderScope")
                    headerImageEntriesByIdx.getOrPut(idx) { mutableListOf() } += entry
                }
                is DocumentContext.ImageOwner.Footer -> {
                    val idx = footerScopeToIdx[owner.scope]
                        ?: error("Image registered against an unallocated FooterScope")
                    footerImageEntriesByIdx.getOrPut(idx) { mutableListOf() } += entry
                }
            }
        }

        // Body images: document-level allocator (existing behavior).
        val bodyImageRids = bodyImageEntries.map { entry ->
            val rid = allocator.nextId()
            entry.slot.resolvedRid = rid
            rid
        }

        // Header/footer images: per-part counters starting at rId0.
        fun resolvePerPart(entries: List<DocumentContext.ImageEntry>):
            List<io.docxkt.api.ImageWithRid> {
            return entries.mapIndexed { i, entry ->
                val rid = "rId$i"
                entry.slot.resolvedRid = rid
                io.docxkt.api.ImageWithRid(image = entry.image, rid = rid)
            }
        }

        val perHeaderImages: Map<Int, List<io.docxkt.api.ImageWithRid>> =
            headerImageEntriesByIdx.mapValues { (_, v) -> resolvePerPart(v) }
        val perFooterImages: Map<Int, List<io.docxkt.api.ImageWithRid>> =
            footerImageEntriesByIdx.mapValues { (_, v) -> resolvePerPart(v) }

        // fontTable rel comes AFTER all dynamic rels (matches upstream's
        // next-compiler behaviour: image rels are appended, then fontTable).
        val fontTableRid = allocator.nextId()

        // Embedded fonts: each font gets a fontTable-LOCAL relationship
        // id (rId1, rId2, …). These do NOT consume slots from the
        // document-level allocator — fontTable.xml has its own rels
        // file scope.
        val embeddedFontList = context.embeddedFonts()
        val embeddedFontsWithRid = embeddedFontList.mapIndexed { idx, font ->
            io.docxkt.api.EmbeddedFontWithRid(font = font, rid = "rId${idx + 1}")
        }

        // Resolve PendingSectionBreak placeholders into real
        // Paragraphs carrying the section's headers/footers.
        var sectionIdx = 0
        val resolvedBlocks = blocks.map { block ->
            if (block is io.docxkt.dsl.PendingSectionBreak) {
                val s = block.scope
                val sectPr = SectionProperties(
                    pageSize = s.pageSizeValue,
                    pageMargins = s.pageMarginsValue,
                    headerRefs = sectionHeaderRefs[sectionIdx],
                    footerRefs = sectionFooterRefs[sectionIdx],
                    pageBorders = s.pageBordersValue,
                    lineNumbering = s.lineNumberingValue,
                    columns = s.columnsValue,
                    titlePage = s.titlePageValue,
                    type = s.typeValue,
                    verticalAlign = s.verticalAlignValue,
                    pageNumbersStart = s.pageNumbersStartValue,
                    pageNumbersFormat = s.pageNumbersFormatValue,
                    pageNumbersSeparator = s.pageNumbersSeparatorValue,
                    pageNumbersChapStyle = s.pageNumbersChapStyleValue,
                )
                sectionIdx += 1
                io.docxkt.model.paragraph.Paragraph(
                    children = emptyList(),
                    properties = io.docxkt.model.paragraph.ParagraphProperties(
                        sectionProperties = sectPr,
                    ),
                )
            } else {
                block
            }
        }

        // Empty-body case: no resolved blocks AND no H/F refs AND no
        // section break in source. Upstream emits <w:body/> for
        // sections=[]; we mirror by passing null sectPr to Body.
        val hasBodyContent = resolvedBlocks.isNotEmpty()
        val hasTrailingSectionConfig = trailingHeaderRefs.isNotEmpty() ||
            trailingFooterRefs.isNotEmpty()
        val emitTrailingSectPr = hasBodyContent || hasTrailingSectionConfig
        val body = Body(
            children = resolvedBlocks,
            sectionProperties = if (emitTrailingSectPr) {
                SectionProperties(
                    pageSize = pageSizeValue,
                    pageMargins = pageMarginsValue,
                    headerRefs = trailingHeaderRefs,
                    footerRefs = trailingFooterRefs,
                    pageBorders = pageBordersValue,
                    lineNumbering = lineNumberingValue,
                    columns = columnsValue,
                    titlePage = titlePageValue,
                    type = sectionTypeValue,
                    verticalAlign = sectionVerticalAlignValue,
                    pageNumbersStart = pageNumbersStartValue,
                    pageNumbersFormat = pageNumbersFormatValue,
                    pageNumbersSeparator = pageNumbersSeparatorValue,
                    pageNumbersChapStyle = pageNumbersChapStyleValue,
                )
            } else {
                null
            },
        )
        // Only body images go to document.xml.rels. Header/footer
        // images are attached to their HeaderEntry/FooterEntry in
        // the .images field below.
        val images = bodyImageEntries.zip(bodyImageRids) { entry, rid ->
            io.docxkt.api.ImageWithRid(image = entry.image, rid = rid)
        }
        val headerEntriesWithImages = headerEntries.mapIndexed { idx, entry ->
            entry.copy(images = perHeaderImages[idx] ?: emptyList())
        }
        val footerEntriesWithImages = footerEntries.mapIndexed { idx, entry ->
            entry.copy(images = perFooterImages[idx] ?: emptyList())
        }
        val numberingTemplates = listTemplates.map {
            io.docxkt.api.NumberingTemplate(
                abstractNumId = it.abstractNumId,
                numId = it.numId,
                levels = it.levels,
            )
        }
        // settings.xml's <w:evenAndOddHeaders/> flag is a global
        // signal. Patch it onto context.settings before
        // serialization. Conservative: set true when document-level
        // even H/F or any per-section even H/F is present.
        val anySectionHasEven = blocks
            .filterIsInstance<io.docxkt.dsl.PendingSectionBreak>()
            .any { it.scope.evenAndOddHeadersValue }
        val effectiveSettings = if (evenAndOddHeadersValue || anySectionHasEven) {
            context.settings.copy(evenAndOddHeaders = true)
        } else {
            context.settings
        }

        return io.docxkt.api.Document(
            body = body,
            headerEntries = headerEntriesWithImages,
            footerEntries = footerEntriesWithImages,
            images = images,
            numberingTemplates = numberingTemplates,
            numberingConcretes = concreteDescriptors,
            numberingRid = numberingRid,
            styles = styles,
            documentDefaults = docDefaults,
            stylesRid = stylesRid,
            hyperlinks = hyperlinkSlots,
            coreProperties = context.coreProperties,
            customProperties = context.customProperties(),
            settingsContent = effectiveSettings,
            settingsRid = settingsRid,
            fontTableRid = fontTableRid,
            embeddedFonts = embeddedFontsWithRid,
            footnotes = userFootnotes,
            footnotesRid = footnotesRid,
            endnotes = userEndnotes,
            endnotesRid = endnotesRid,
            comments = userComments,
            commentsRid = commentsRid,
            backgroundColor = backgroundColorValue,
        )
    }

    private var backgroundColorValue: String? = null

    /**
     * Set the document background color (`<w:background w:color="…"/>`).
     * Hex RGB without leading `#` (e.g. `"C45911"`) or `"auto"`.
     * Equivalent to upstream's `new Document({ background: { color: … } })`.
     */
    public fun background(color: String) {
        backgroundColorValue = color
    }

    internal var headerValue: io.docxkt.model.header.Header? = null
    internal var firstHeaderValue: io.docxkt.model.header.Header? = null
    internal var evenHeaderValue: io.docxkt.model.header.Header? = null
    internal var footerValue: io.docxkt.model.footer.Footer? = null
    internal var firstFooterValue: io.docxkt.model.footer.Footer? = null
    internal var evenFooterValue: io.docxkt.model.footer.Footer? = null
    internal var titlePageValue: Boolean = false
    internal var evenAndOddHeadersValue: Boolean = false

    /**
     * Configure a header. [type] selects which page(s) the header
     * applies to (default / first / even). Calling twice with the
     * same [type] replaces the previous value — one header per
     * type per document section.
     *
     * Setting a `FIRST` header automatically enables `<w:titlePg/>`
     * in the section. Setting an `EVEN` header automatically enables
     * `<w:evenAndOddHeaders/>` in `word/settings.xml`.
     */
    public fun header(
        type: HeaderFooterReferenceType = HeaderFooterReferenceType.DEFAULT,
        configure: HeaderScope.() -> Unit,
    ) {
        val scope = HeaderScope(context)
        scope.configure()
        val built = scope.build()
        when (type) {
            HeaderFooterReferenceType.DEFAULT -> headerValue = built
            HeaderFooterReferenceType.FIRST -> firstHeaderValue = built
            HeaderFooterReferenceType.EVEN -> {
                evenHeaderValue = built
                evenAndOddHeadersValue = true
            }
        }
    }

    /**
     * Configure a footer. Setting `EVEN` auto-enables
     * `<w:evenAndOddHeaders/>` (global). `FIRST` does NOT auto-enable
     * `<w:titlePg/>` — call [titlePage] explicitly when needed.
     */
    public fun footer(
        type: HeaderFooterReferenceType = HeaderFooterReferenceType.DEFAULT,
        configure: FooterScope.() -> Unit,
    ) {
        val scope = FooterScope(context)
        scope.configure()
        val built = scope.build()
        when (type) {
            HeaderFooterReferenceType.DEFAULT -> footerValue = built
            HeaderFooterReferenceType.FIRST -> firstFooterValue = built
            HeaderFooterReferenceType.EVEN -> {
                evenFooterValue = built
                evenAndOddHeadersValue = true
            }
        }
    }

    /** Enable `<w:titlePg/>` on the document-level (trailing) section. */
    public fun titlePage() {
        titlePageValue = true
    }
}
