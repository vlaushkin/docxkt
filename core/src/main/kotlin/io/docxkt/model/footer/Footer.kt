// Port of: src/file/footer/footer.ts (the model — block-level
// children list).
package io.docxkt.model.footer

import io.docxkt.xml.XmlComponent

/**
 * The content model of `word/footer{id}.xml` — a list of block-level
 * children (paragraphs, tables, and any future block-level construct).
 */
internal class Footer(
    val children: List<XmlComponent>,
)
