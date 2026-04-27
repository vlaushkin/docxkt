// Port of: src/file/header/header.ts (the model — block-level
// children list).
package io.docxkt.model.header

import io.docxkt.xml.XmlComponent

/**
 * The content model of `word/header{id}.xml` — a list of block-level
 * children (paragraphs, tables, and any future block-level construct).
 */
internal class Header(
    val children: List<XmlComponent>,
)
