// No upstream analogue — :core's Paragraph type emits self-contained
// XML (no namespace declarations on the <w:p>); the patcher needs
// the element imported into the target Document with namespaces
// resolved from a wrapping envelope. We do that by parsing through
// a temporary <w:document> root and importing the inner <w:p>.
package io.docxkt.patcher.replace

import io.docxkt.patcher.io.OoxmlParser
import io.docxkt.xml.Namespaces
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Parses one of [io.docxkt.api.ParagraphSnippets.toXml] strings
 * into a DOM `<w:p>` Element ready to splice into a target
 * Document.
 *
 * The snippet XML produced by `:core` looks like
 * `<w:p>…</w:p>` with no namespace declarations of its own. To
 * parse it correctly (so the namespace URIs resolve), we wrap it
 * in a tiny `<w:document>` envelope that declares `xmlns:w` and
 * the related namespaces, then strip back to the inner paragraph.
 */
internal object ParagraphSnippetParser {

    private val ENVELOPE_NAMESPACES =
        "xmlns:w=\"${Namespaces.WORDPROCESSING_ML}\"" +
            " xmlns:mc=\"${Namespaces.MARKUP_COMPATIBILITY}\"" +
            " xmlns:r=\"${Namespaces.RELATIONSHIPS_OFFICE_DOCUMENT}\"" +
            " xmlns:wp=\"${Namespaces.WORDPROCESSING_DRAWING}\"" +
            " xmlns:a=\"${Namespaces.DRAWINGML_MAIN}\"" +
            " xmlns:pic=\"${Namespaces.DRAWINGML_PICTURE}\""

    /**
     * Parse [paragraphXml] (a `<w:p>…</w:p>` string from
     * [io.docxkt.api.ParagraphSnippets.toXml]) and return a paragraph
     * Element imported into [target].
     */
    fun parseAndImport(paragraphXml: String, target: Document): Element =
        parseAndImportElement(paragraphXml, target, "p")

    /**
     * Parse a snippet that may be either a `<w:p>` or a `<w:tbl>`
     * (Patch.Paragraphs admits both). Returns whichever top-level
     * element is found, imported into [target].
     */
    fun parseAndImportBlock(snippetXml: String, target: Document): Element {
        val envelopedXml = """<?xml version="1.0" encoding="UTF-8"?><w:document $ENVELOPE_NAMESPACES>$snippetXml</w:document>"""
        val parsed = OoxmlParser.parse(envelopedXml.toByteArray(Charsets.UTF_8))
        val root = parsed.documentElement
        val children = root.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i)
            if (n.nodeType == Node.ELEMENT_NODE &&
                n.namespaceURI == Namespaces.WORDPROCESSING_ML &&
                (n.localName == "p" || n.localName == "tbl")
            ) {
                return target.importNode(n, /* deep = */ true) as Element
            }
        }
        error("No <w:p> or <w:tbl> child found in snippet: $snippetXml")
    }

    /**
     * Parse a `<w:r>…</w:r>` string and return the run Element
     * imported into [target]. Used by [ImageInjector] to splice
     * a drawing run into a paragraph.
     */
    fun parseAndImportRun(runXml: String, target: Document): Element =
        parseAndImportElement(runXml, target, "r")

    /**
     * Parse a `<w:tr>…</w:tr>` string and return the row Element
     * imported into [target]. Used by [RowInjector] to splice
     * snippet rows into a table.
     */
    fun parseAndImportRow(rowXml: String, target: Document): Element =
        parseAndImportElement(rowXml, target, "tr")

    private fun parseAndImportElement(snippetXml: String, target: Document, expectedLocalName: String): Element {
        // Drawing snippets need wp/a/pic namespaces in addition to
        // the basic w/mc/r — the [ENVELOPE_NAMESPACES] string declares
        // a generous set so any upstream-shaped run element parses.
        val envelopedXml = """<?xml version="1.0" encoding="UTF-8"?><w:document $ENVELOPE_NAMESPACES>$snippetXml</w:document>"""
        val parsed = OoxmlParser.parse(envelopedXml.toByteArray(Charsets.UTF_8))
        val root = parsed.documentElement
        val children = root.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i)
            if (n.nodeType == Node.ELEMENT_NODE &&
                n.namespaceURI == Namespaces.WORDPROCESSING_ML &&
                n.localName == expectedLocalName
            ) {
                return target.importNode(n, /* deep = */ true) as Element
            }
        }
        error("No <w:$expectedLocalName> child found in snippet: $snippetXml")
    }
}
