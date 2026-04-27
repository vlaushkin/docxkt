// No upstream analogue — a shared mutable context used to thread
// image / list registration across DSL scopes.
package io.docxkt.dsl

import io.docxkt.model.comment.Comment
import io.docxkt.model.drawing.Image
import io.docxkt.model.drawing.ImageSlot
import io.docxkt.model.footnote.Footnote
import io.docxkt.model.hyperlink.HyperlinkSlot
import io.docxkt.model.metadata.CoreProperties
import io.docxkt.model.metadata.CustomProperty
import io.docxkt.model.metadata.Settings
import io.docxkt.model.numbering.NumberingLevel
import io.docxkt.model.numbering.NumberingReferenceSlot
import io.docxkt.model.style.Style

/**
 * Shared context threaded from [DocumentScope] through
 * [ParagraphScope] to [RunScope]. Collects deferred-resolution
 * registrations — each entry carries a placeholder that
 * `Document.buildDocument()` fills in once rIds / numIds are
 * allocated.
 *
 * Not thread-safe; callers serialize.
 */
internal class DocumentContext {
    private val imageEntries = mutableListOf<ImageEntry>()
    private val listTemplates = mutableListOf<ListTemplate>()
    private val listTemplateByReference = mutableMapOf<String, ListTemplate>()
    private val pendingNumberingRefs = mutableListOf<NumberingReferenceSlot>()
    private val styleList = mutableListOf<Style>()
    private val styleById = mutableMapOf<String, Style>()
    private val hyperlinkSlots = mutableListOf<HyperlinkSlot>()
    private val embeddedFonts = mutableListOf<io.docxkt.model.font.EmbeddedFont>()
    private val embeddedFontByName = mutableMapOf<String, io.docxkt.model.font.EmbeddedFont>()

    /**
     * Identifies the part that "owns" an image relationship — body
     * images live in `word/_rels/document.xml.rels`, header/footer
     * images in their own `word/_rels/{header|footer}{idx}.xml.rels`.
     *
     * Upstream uses per-file rId allocators starting at `rId0`, with
     * the media binary itself shared by content hash across parts.
     */
    sealed class ImageOwner {
        object Body : ImageOwner()
        data class Header(val scope: HeaderScope) : ImageOwner()
        data class Footer(val scope: FooterScope) : ImageOwner()
    }

    /** Stack of active image owners, top is the current one. */
    private val imageOwnerStack = ArrayDeque<ImageOwner>().apply { addLast(ImageOwner.Body) }
    val currentImageOwner: ImageOwner get() = imageOwnerStack.last()

    fun pushImageOwner(owner: ImageOwner) {
        imageOwnerStack.addLast(owner)
    }

    fun popImageOwner() {
        imageOwnerStack.removeLast()
    }

    /**
     * Identity map from a built [io.docxkt.model.header.Header] /
     * [io.docxkt.model.footer.Footer] back to the originating
     * HeaderScope/FooterScope. Populated in `HeaderScope.build()` /
     * `FooterScope.build()`. Used at document-assembly time to
     * resolve image-owner identity to a part index.
     */
    private val headerSourceScope = mutableMapOf<io.docxkt.model.header.Header, HeaderScope>()
    private val footerSourceScope = mutableMapOf<io.docxkt.model.footer.Footer, FooterScope>()

    fun bindHeaderScope(header: io.docxkt.model.header.Header, scope: HeaderScope) {
        headerSourceScope[header] = scope
    }

    fun bindFooterScope(footer: io.docxkt.model.footer.Footer, scope: FooterScope) {
        footerSourceScope[footer] = scope
    }

    fun headerScopeOf(header: io.docxkt.model.header.Header): HeaderScope? =
        headerSourceScope[header]

    fun footerScopeOf(footer: io.docxkt.model.footer.Footer): FooterScope? =
        footerSourceScope[footer]

    data class ImageEntry(val image: Image, val slot: ImageSlot, val owner: ImageOwner)

    /**
     * One registered list template — a pair of (abstract, concrete)
     * identified by a user-supplied reference string. The
     * `abstractNumId` and `numId` are assigned at
     * `Document.buildDocument()` time.
     */
    internal class ListTemplate(
        val reference: String,
        val levels: List<NumberingLevel>,
    ) {
        var abstractNumId: Int = 0
        var numId: Int = 0
    }

    // --- Images -------------------------------------------------------------

    fun registerImage(image: Image): ImageSlot {
        // Dedupe by image-bytes hash AND owner so each part gets its
        // own rId for the same image. The media binary itself dedupes
        // on bytes alone (one file per content hash), but each part's
        // rels references it via its own rId.
        val owner = currentImageOwner
        val key = Triple(image.bytes.contentHashCode().toLong(), image.format, owner)
        val existing = imageEntriesByKey[key]
        if (existing != null) return existing.slot
        val slot = ImageSlot()
        val entry = ImageEntry(image = image, slot = slot, owner = owner)
        imageEntries += entry
        imageEntriesByKey[key] = entry
        return slot
    }

    private val imageEntriesByKey = mutableMapOf<Triple<Long, io.docxkt.model.drawing.ImageFormat, ImageOwner>, ImageEntry>()

    fun images(): List<ImageEntry> = imageEntries.toList()

    // --- Numbering ----------------------------------------------------------

    /**
     * Register a list template. Calling with the same reference
     * twice replaces the earlier template — matches upstream's
     * "one config per reference" invariant.
     */
    fun registerListTemplate(reference: String, levels: List<NumberingLevel>) {
        val existing = listTemplateByReference[reference]
        if (existing != null) {
            listTemplates.remove(existing)
        }
        val template = ListTemplate(reference = reference, levels = levels)
        listTemplates += template
        listTemplateByReference[reference] = template
    }

    fun listTemplates(): List<ListTemplate> = listTemplates.toList()

    fun findListTemplate(reference: String): ListTemplate? =
        listTemplateByReference[reference]

    /**
     * Register a pending `<w:numPr>` reference from a paragraph.
     * Returns a slot whose `resolvedNumId` will be filled at
     * `Document.buildDocument()` time.
     */
    fun registerNumberingReference(
        reference: String,
        level: Int,
        instance: Int = 0,
        inHeaderFooter: Boolean = false,
    ): NumberingReferenceSlot {
        val slot = NumberingReferenceSlot(
            reference = reference,
            level = level,
            instance = instance,
            inHeaderFooter = inHeaderFooter,
        )
        pendingNumberingRefs += slot
        return slot
    }

    /**
     * Scope-tag flag. HeaderScope/FooterScope toggle this around
     * their `paragraph { }` blocks so any numbering refs registered
     * inside get the H/F flag.
     */
    var inHeaderFooterScope: Boolean = false

    fun pendingNumberingReferences(): List<NumberingReferenceSlot> =
        pendingNumberingRefs.toList()

    // --- Styles -------------------------------------------------------------

    /**
     * Register a style. Calling with the same style id twice replaces
     * the earlier definition (matches upstream's "last wins"
     * behaviour when the same id is registered twice). Paragraph and
     * character styles share the same id namespace.
     */
    fun registerStyle(style: Style) {
        val existing = styleById[style.id]
        if (existing != null) {
            styleList.remove(existing)
        }
        styleList += style
        styleById[style.id] = style
    }

    fun styles(): List<Style> = styleList.toList()

    // --- Document defaults --------------------------------------------------
    var documentDefaults: io.docxkt.model.style.DocumentDefaults? = null

    // --- Hyperlinks ---------------------------------------------------------

    /**
     * Register a hyperlink call site. Returns a fresh slot whose
     * `resolvedRid` is filled at `Document.buildDocument()` time.
     * Each call site gets its own rId — two hyperlinks pointing at
     * the same URL still produce two relationships (matches
     * upstream's per-call-site `uniqueId()` behaviour).
     */
    fun registerHyperlink(target: String): HyperlinkSlot {
        val slot = HyperlinkSlot(target = target)
        hyperlinkSlots += slot
        return slot
    }

    fun hyperlinks(): List<HyperlinkSlot> = hyperlinkSlots.toList()

    // --- Bookmarks ----------------------------------------------------------

    private var bookmarkIdCounter: Int = 0
    private val bookmarkIdByName = mutableMapOf<String, Int>()

    /**
     * Register a new bookmark by name, returning its freshly-allocated
     * numeric id. Errors if the name is already in use — OOXML
     * requires bookmark names to be unique within a document.
     */
    fun registerBookmark(name: String): Int {
        require(name !in bookmarkIdByName) {
            "bookmark name '$name' already registered — OOXML requires unique bookmark names per document"
        }
        bookmarkIdCounter += 1
        bookmarkIdByName[name] = bookmarkIdCounter
        return bookmarkIdCounter
    }

    /**
     * Look up the numeric id of a previously-registered bookmark by
     * name. Returns `null` for an unknown name — callers should error
     * loudly when this happens (an orphan bookmarkEnd is a bug).
     */
    fun findBookmarkId(name: String): Int? = bookmarkIdByName[name]

    // --- Metadata -----------------------------------------------------------

    /**
     * Core properties (title/creator/timestamps). Defaults to an
     * empty [CoreProperties] — the Kotlin `CorePropertiesPart`
     * applies upstream's "Un-named" / revision=1 fallbacks when
     * unset.
     */
    var coreProperties: CoreProperties = CoreProperties()

    private val customProps = mutableListOf<CustomProperty>()

    fun registerCustomProperty(name: String, value: String) {
        customProps += CustomProperty(name = name, value = value)
    }

    fun customProperties(): List<CustomProperty> = customProps.toList()

    /** Document-level settings — the `<w:settings>` part contents. */
    var settings: Settings = Settings()

    // --- Footnotes / Endnotes -----------------------------------------------

    private val footnotes = mutableListOf<Footnote>()
    private val endnotes = mutableListOf<Footnote>()

    /** Register a user footnote. Caller provides the numeric id. */
    fun registerFootnote(note: Footnote) {
        footnotes += note
    }

    /** Register a user endnote. Caller provides the numeric id. */
    fun registerEndnote(note: Footnote) {
        endnotes += note
    }

    fun footnotes(): List<Footnote> = footnotes.toList()
    fun endnotes(): List<Footnote> = endnotes.toList()

    // --- Comments -----------------------------------------------------------

    private val comments = mutableListOf<Comment>()

    fun registerComment(comment: Comment) {
        comments += comment
    }

    fun comments(): List<Comment> = comments.toList()

    // --- Track revisions ----------------------------------------------------

    private var revisionIdCounter: Int = 0

    /**
     * Allocate the next monotonic revision id for an
     * `<w:ins>` / `<w:del>` wrapper. Ids are local to the
     * document — no rId namespace collision.
     */
    fun nextRevisionId(): Int {
        revisionIdCounter += 1
        return revisionIdCounter
    }

    // --- Embedded fonts -----------------------------------------------------

    fun registerEmbeddedFont(font: io.docxkt.model.font.EmbeddedFont) {
        // Dedupe by name — calling twice with the same name replaces.
        val existing = embeddedFontByName[font.name]
        if (existing != null) {
            embeddedFonts.remove(existing)
        }
        embeddedFonts += font
        embeddedFontByName[font.name] = font
    }

    fun embeddedFonts(): List<io.docxkt.model.font.EmbeddedFont> = embeddedFonts.toList()
}
