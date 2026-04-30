// Port of: src/file/file.ts (top-level Document) +
//          src/export/packer/ (writeTo/toBuffer equivalents)
package io.docxkt.api

import io.docxkt.dsl.DocumentScope
import io.docxkt.model.Body
import io.docxkt.model.drawing.Image
import io.docxkt.model.footer.Footer
import io.docxkt.model.header.Header
import io.docxkt.model.numbering.AbstractNumbering
import io.docxkt.model.numbering.ConcreteNumbering
import io.docxkt.model.numbering.NumberingLevel
import io.docxkt.model.comment.Comment
import io.docxkt.model.footnote.Footnote
import io.docxkt.model.metadata.CoreProperties
import io.docxkt.model.metadata.CustomProperty
import io.docxkt.model.metadata.Settings
import io.docxkt.pack.DocxPackager
import io.docxkt.part.AppPropertiesPart
import io.docxkt.part.ContentTypesPart
import io.docxkt.part.CorePropertiesPart
import io.docxkt.part.CustomPropertiesPart
import io.docxkt.part.DocumentRelTypes
import io.docxkt.part.DocumentRelsPart
import io.docxkt.part.CommentsPart
import io.docxkt.part.EndnotesPart
import io.docxkt.part.FontTablePart
import io.docxkt.part.FooterPart
import io.docxkt.part.FootnotesPart
import io.docxkt.part.HeaderPart
import io.docxkt.part.MainDocumentPart
import io.docxkt.part.NumberingPart
import io.docxkt.part.PackageRelTypes
import io.docxkt.part.PackageRelsPart
import io.docxkt.part.SettingsPart
import io.docxkt.part.StylesPart

/**
 * An image paired with the relationship id the DSL allocated for it
 * at `DocumentScope.buildDocument()` time.
 */
internal data class ImageWithRid(val image: Image, val rid: String)

/**
 * One embedded font with its allocated fontTable-LOCAL relationship
 * id (rId1+ inside `word/_rels/fontTable.xml.rels`).
 */
internal data class EmbeddedFontWithRid(
    val font: io.docxkt.model.font.EmbeddedFont,
    val rid: String,
)

/**
 * A registered header / footer with its allocated rId. Documents
 * may carry many such entries (one per section per type).
 *
 * Each entry carries its own [images] — relationships scoped to
 * that part's own `_rels/{header|footer}{idx}.xml.rels` file,
 * with rIds starting at `rId0` per upstream's behaviour.
 */
internal data class HeaderEntry(
    val header: Header,
    val rid: String,
    val images: List<ImageWithRid> = emptyList(),
)

internal data class FooterEntry(
    val footer: Footer,
    val rid: String,
    val images: List<ImageWithRid> = emptyList(),
)

/**
 * A registered list template with its allocated abstractNumId and
 * numId. Built by `DocumentScope.buildDocument()` and handed to the
 * `Document` so the assembly can emit the numbering part.
 */
internal data class NumberingTemplate(
    val abstractNumId: Int,
    val numId: Int,
    val levels: List<NumberingLevel>,
)

/**
 * One concrete `<w:num>` to emit. Built by
 * `DocumentScope.buildDocument()` from each unique
 * `(reference, instance)` paragraph reference encountered in the
 * pending-resolution list.
 */
internal data class ConcreteNumberingDescriptor(
    val numId: Int,
    val abstractNumId: Int,
    val startOverride: Int,
)

/** Type URI for image relationships inside `document.xml.rels`. */
internal const val IMAGE_RELATIONSHIP_TYPE: String = io.docxkt.xml.Namespaces.REL_IMAGE

/**
 * A docxkt document.
 *
 * Built via the [document] DSL. Carries the assembled model and the
 * parts needed to serialize it. `writeTo` / `toByteArray` serialize
 * to a `.docx` ZIP container.
 *
 * Inline images each carry their own rId allocated at
 * `DocumentScope.buildDocument()` time. Media bytes are written as
 * `word/media/image{N}.{ext}` in insertion order.
 */
public class Document internal constructor(
    internal val body: Body,
    /**
     * All headers (across all sections) in document-walk order.
     * Each entry carries its rId; section properties reference it
     * by rId.
     */
    internal val headerEntries: List<HeaderEntry> = emptyList(),
    /** All footers (across all sections), same shape as [headerEntries]. */
    internal val footerEntries: List<FooterEntry> = emptyList(),
    internal val images: List<ImageWithRid> = emptyList(),
    internal val numberingTemplates: List<NumberingTemplate> = emptyList(),
    internal val numberingConcretes: List<ConcreteNumberingDescriptor> = emptyList(),
    internal val numberingRid: String? = null,
    internal val styles: List<io.docxkt.model.style.Style> = emptyList(),
    internal val documentDefaults: io.docxkt.model.style.DocumentDefaults? = null,
    internal val stylesRid: String? = null,
    internal val hyperlinks: List<io.docxkt.model.hyperlink.HyperlinkSlot> = emptyList(),
    // Metadata & auxiliary parts.
    internal val coreProperties: CoreProperties = CoreProperties(),
    internal val customProperties: List<CustomProperty> = emptyList(),
    internal val settingsContent: Settings = Settings(),
    internal val settingsRid: String? = null,
    internal val fontTableRid: String? = null,
    internal val embeddedFonts: List<EmbeddedFontWithRid> = emptyList(),
    // Footnotes / endnotes.
    internal val footnotes: List<Footnote> = emptyList(),
    internal val footnotesRid: String? = null,
    internal val endnotes: List<Footnote> = emptyList(),
    internal val endnotesRid: String? = null,
    // Comments.
    internal val comments: List<Comment> = emptyList(),
    internal val commentsRid: String? = null,
    // Document-level background color.
    internal val backgroundColor: String? = null,
) {
    internal fun assembleEntries(): List<DocxPackager.Entry> {
        val entries = mutableListOf<DocxPackager.Entry>()

        val mainDoc = MainDocumentPart(body, backgroundColor = backgroundColor)
        entries += DocxPackager.Entry(mainDoc.path, mainDoc.toBytes())

        val rels = mutableListOf<DocumentRelsPart.Relationship>()
        val extraOverrides = mutableListOf<ContentTypesPart.Override>()
        val extraDefaults = mutableListOf<ContentTypesPart.Default>()

        // Assemble a global "media path table" — one row per unique
        // image (by content-hash + format), shared across body /
        // header / footer parts. Sequential numbering so the first
        // image we see is image1.{ext}.
        val mediaPathTable = LinkedHashMap<Pair<Long, String>, String>()
        val mediaBytesByPath = LinkedHashMap<String, ByteArray>()
        val seenExtensions = mutableSetOf<String>()
        fun mediaPathFor(image: io.docxkt.model.drawing.Image): String {
            val key = image.bytes.contentHashCode().toLong() to image.format.extension
            return mediaPathTable.getOrPut(key) {
                val idx = mediaPathTable.size + 1
                val rel = "media/image$idx.${image.format.extension}"
                mediaBytesByPath["word/$rel"] = image.bytes
                if (seenExtensions.add(image.format.extension)) {
                    extraDefaults += ContentTypesPart.Default(
                        extension = image.format.extension,
                        contentType = image.format.mimeType,
                    )
                }
                rel
            }
        }

        // Headers / footers — emit one part per entry. Order in the
        // list matches document-walk order (per-section in source
        // order, default → first → even within a section).
        headerEntries.forEachIndexed { idx, entry ->
            val hid = idx + 1
            val partName = "header$hid.xml"
            val headerPart = HeaderPart(id = hid, header = entry.header)
            entries += DocxPackager.Entry(headerPart.path, headerPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = entry.rid,
                type = DocumentRelTypes.HEADER,
                target = partName,
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/$partName",
                contentType = ContentTypesPart.CONTENT_TYPE_HEADER,
            )
            // Per-part rels for images registered against this header.
            if (entry.images.isNotEmpty()) {
                val partRels = entry.images.map { img ->
                    DocumentRelsPart.Relationship(
                        id = img.rid,
                        type = IMAGE_RELATIONSHIP_TYPE,
                        target = mediaPathFor(img.image),
                    )
                }
                val relsPart = io.docxkt.part.HeaderRelsPart(headerIndex = hid, relationships = partRels)
                entries += DocxPackager.Entry(relsPart.path, relsPart.toBytes())
            }
        }
        footerEntries.forEachIndexed { idx, entry ->
            val fid = idx + 1
            val partName = "footer$fid.xml"
            val footerPart = FooterPart(id = fid, footer = entry.footer)
            entries += DocxPackager.Entry(footerPart.path, footerPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = entry.rid,
                type = DocumentRelTypes.FOOTER,
                target = partName,
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/$partName",
                contentType = ContentTypesPart.CONTENT_TYPE_FOOTER,
            )
            if (entry.images.isNotEmpty()) {
                val partRels = entry.images.map { img ->
                    DocumentRelsPart.Relationship(
                        id = img.rid,
                        type = IMAGE_RELATIONSHIP_TYPE,
                        target = mediaPathFor(img.image),
                    )
                }
                val relsPart = io.docxkt.part.FooterRelsPart(footerIndex = fid, relationships = partRels)
                entries += DocxPackager.Entry(relsPart.path, relsPart.toBytes())
            }
        }

        // Emit numbering.xml when EITHER user templates OR pending
        // references exist (numberingRid non-null signals emit). When
        // emitted, the phantom default-bullet abstractNum + concrete
        // are prepended.
        if (numberingRid != null) {
            val abstracts = listOf(io.docxkt.model.numbering.DefaultBulletNumbering.ABSTRACT) +
                numberingTemplates.map {
                    AbstractNumbering(abstractNumId = it.abstractNumId, levels = it.levels)
                }
            val concretes = listOf(io.docxkt.model.numbering.DefaultBulletNumbering.CONCRETE) +
                numberingConcretes.map {
                    ConcreteNumbering(
                        numId = it.numId,
                        abstractNumId = it.abstractNumId,
                        startOverride = it.startOverride,
                    )
                }
            val numberingPart = NumberingPart(
                abstracts = abstracts,
                concretes = concretes,
            )
            entries += DocxPackager.Entry(numberingPart.path, numberingPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = numberingRid
                    ?: error("Document carries numbering templates but numberingRid is null"),
                type = DocumentRelTypes.NUMBERING,
                target = "numbering.xml",
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/numbering.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_NUMBERING,
            )
        }

        if (styles.isNotEmpty() || documentDefaults != null) {
            val stylesPart = StylesPart(styles = styles, documentDefaults = documentDefaults)
            entries += DocxPackager.Entry(stylesPart.path, stylesPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = stylesRid
                    ?: error("Document carries styles but stylesRid is null"),
                type = DocumentRelTypes.STYLES,
                target = "styles.xml",
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/styles.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_STYLES,
            )
        }

        for (slot in hyperlinks) {
            rels += DocumentRelsPart.Relationship(
                id = slot.resolvedRid
                    ?: error("Hyperlink slot target='${slot.target}' was not resolved"),
                type = DocumentRelTypes.HYPERLINK,
                target = slot.target,
                targetMode = "External",
            )
        }

        // Body images use the shared mediaPathFor table so they
        // coordinate filenames with header/footer images by content
        // hash. Each unique image binary is written once.
        for (entry in images) {
            val target = mediaPathFor(entry.image)
            rels += DocumentRelsPart.Relationship(
                id = entry.rid,
                type = IMAGE_RELATIONSHIP_TYPE,
                target = target,
            )
        }

        // settings.xml and fontTable.xml — both unconditional (every
        // well-formed .docx ships them).
        val settingsPart = SettingsPart(settings = settingsContent)
        entries += DocxPackager.Entry(settingsPart.path, settingsPart.toBytes())
        rels += DocumentRelsPart.Relationship(
            id = settingsRid ?: error("settingsRid is null"),
            type = DocumentRelTypes.SETTINGS,
            target = "settings.xml",
        )
        extraOverrides += ContentTypesPart.Override(
            partName = "/word/settings.xml",
            contentType = ContentTypesPart.CONTENT_TYPE_SETTINGS,
        )

        val fontEmbeds = embeddedFonts.map {
            io.docxkt.part.EmbeddedFontEmbed(font = it.font, rid = it.rid)
        }
        val fontTablePart = FontTablePart(embeds = fontEmbeds)
        entries += DocxPackager.Entry(fontTablePart.path, fontTablePart.toBytes())
        rels += DocumentRelsPart.Relationship(
            id = fontTableRid ?: error("fontTableRid is null"),
            type = DocumentRelTypes.FONT_TABLE,
            target = "fontTable.xml",
        )
        extraOverrides += ContentTypesPart.Override(
            partName = "/word/fontTable.xml",
            contentType = ContentTypesPart.CONTENT_TYPE_FONT_TABLE,
        )

        // Embedded font binaries + fontTable rels.
        if (embeddedFonts.isNotEmpty()) {
            val fontTableRelsPart = io.docxkt.part.FontTableRelsPart(
                fonts = embeddedFonts,
            )
            entries += DocxPackager.Entry(fontTableRelsPart.path, fontTableRelsPart.toBytes())
            for (entry in embeddedFonts) {
                val path = "word/fonts/${entry.font.name}.odttf"
                entries += DocxPackager.Entry(path, entry.font.obfuscatedBytes)
            }
            extraDefaults += ContentTypesPart.Default(
                extension = "odttf",
                contentType = ContentTypesPart.CONTENT_TYPE_OBFUSCATED_FONT,
            )
        }

        // Footnotes / endnotes parts (conditional).
        if (footnotes.isNotEmpty()) {
            val footnotesPart = FootnotesPart(userFootnotes = footnotes)
            entries += DocxPackager.Entry(footnotesPart.path, footnotesPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = footnotesRid ?: error("footnotesRid is null"),
                type = DocumentRelTypes.FOOTNOTES,
                target = "footnotes.xml",
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/footnotes.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_FOOTNOTES,
            )
        }
        if (endnotes.isNotEmpty()) {
            val endnotesPart = EndnotesPart(userEndnotes = endnotes)
            entries += DocxPackager.Entry(endnotesPart.path, endnotesPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = endnotesRid ?: error("endnotesRid is null"),
                type = DocumentRelTypes.ENDNOTES,
                target = "endnotes.xml",
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/endnotes.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_ENDNOTES,
            )
        }
        // Comments part + rel + override (conditional).
        if (comments.isNotEmpty()) {
            val commentsPart = CommentsPart(comments = comments)
            entries += DocxPackager.Entry(commentsPart.path, commentsPart.toBytes())
            rels += DocumentRelsPart.Relationship(
                id = commentsRid ?: error("commentsRid is null"),
                type = DocumentRelTypes.COMMENTS,
                target = "comments.xml",
            )
            extraOverrides += ContentTypesPart.Override(
                partName = "/word/comments.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_COMMENTS,
            )
        }

        // docProps parts (core, app, and conditionally custom).
        val corePart = CorePropertiesPart(properties = coreProperties)
        entries += DocxPackager.Entry(corePart.path, corePart.toBytes())
        extraOverrides += ContentTypesPart.Override(
            partName = "/docProps/core.xml",
            contentType = ContentTypesPart.CONTENT_TYPE_CORE_PROPERTIES,
        )

        val appPart = AppPropertiesPart()
        entries += DocxPackager.Entry(appPart.path, appPart.toBytes())
        extraOverrides += ContentTypesPart.Override(
            partName = "/docProps/app.xml",
            contentType = ContentTypesPart.CONTENT_TYPE_EXTENDED_PROPERTIES,
        )

        val hasCustomProps = customProperties.isNotEmpty()
        if (hasCustomProps) {
            val customPart = CustomPropertiesPart(entries = customProperties)
            entries += DocxPackager.Entry(customPart.path, customPart.toBytes())
            extraOverrides += ContentTypesPart.Override(
                partName = "/docProps/custom.xml",
                contentType = ContentTypesPart.CONTENT_TYPE_CUSTOM_PROPERTIES,
            )
        }

        // Write each unique media binary once. Path was assigned by
        // mediaPathFor() above (called in document / header / footer
        // paths).
        for ((path, bytes) in mediaBytesByPath) {
            entries += DocxPackager.Entry(path, bytes)
        }

        val documentRels = DocumentRelsPart(rels)
        if (documentRels.isNonEmpty) {
            entries += DocxPackager.Entry(documentRels.path, documentRels.toBytes())
        }

        val contentTypes = ContentTypesPart(
            extraDefaults = extraDefaults,
            extraOverrides = extraOverrides,
        )
        entries += DocxPackager.Entry(contentTypes.path, contentTypes.toBytes())

        // Package-level rels: officeDocument (always) + three
        // metadata rels. rId order mirrors upstream's emit sequence.
        val pkgRels = mutableListOf(
            PackageRelsPart.Relationship(
                id = "rId1",
                type = PackageRelTypes.OFFICE_DOCUMENT,
                target = "word/document.xml",
            ),
            PackageRelsPart.Relationship(
                id = "rId2",
                type = PackageRelTypes.CORE_PROPERTIES,
                target = "docProps/core.xml",
            ),
            PackageRelsPart.Relationship(
                id = "rId3",
                type = PackageRelTypes.EXTENDED_PROPERTIES,
                target = "docProps/app.xml",
            ),
        )
        if (hasCustomProps) {
            pkgRels += PackageRelsPart.Relationship(
                id = "rId4",
                type = PackageRelTypes.CUSTOM_PROPERTIES,
                target = "docProps/custom.xml",
            )
        }
        val packageRels = PackageRelsPart(pkgRels)
        entries += DocxPackager.Entry(packageRels.path, packageRels.toBytes())

        return entries
    }
}

/**
 * Top-level DSL entry point. Usage:
 *
 * ```
 * val doc = document {
 *     paragraph { text("Hello, world!") }
 * }
 * doc.writeTo(file)
 * ```
 */
public fun document(configure: DocumentScope.() -> Unit): Document {
    val scope = DocumentScope()
    scope.configure()
    return scope.buildDocument()
}
