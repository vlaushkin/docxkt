// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.footer.Footer
import io.docxkt.model.header.Header
import io.docxkt.xml.XmlComponent

/**
 * Builder for a `<w:hdr>`. Collects block-level children
 * (paragraphs and tables) via the existing scopes.
 *
 * Takes the enclosing [DocumentContext] so paragraphs inside
 * the header can resolve numbering references / register images
 * against the same registries the body uses, and pushes itself
 * onto the context's image-owner stack so any image registered
 * inside this header gets a per-part rId (resolved against
 * `word/_rels/header{idx}.xml.rels`).
 */
@DocxktDsl
public class HeaderScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    private val children = mutableListOf<XmlComponent>()

    /** Add a paragraph to the header. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val prev = context.inHeaderFooterScope
        context.inHeaderFooterScope = true
        context.pushImageOwner(DocumentContext.ImageOwner.Header(this))
        try {
            val scope = ParagraphScope(context)
            scope.configure()
            children += scope.build()
        } finally {
            context.popImageOwner()
            context.inHeaderFooterScope = prev
        }
    }

    /** Add a table to the header. */
    public fun table(configure: TableScope.() -> Unit) {
        val prev = context.inHeaderFooterScope
        context.inHeaderFooterScope = true
        context.pushImageOwner(DocumentContext.ImageOwner.Header(this))
        try {
            val scope = TableScope(context)
            scope.configure()
            children += scope.build()
        } finally {
            context.popImageOwner()
            context.inHeaderFooterScope = prev
        }
    }

    internal fun build(): Header {
        val header = Header(children = children.toList())
        context.bindHeaderScope(header, this)
        return header
    }
}

/** Builder for a `<w:ftr>`. Same shape as [HeaderScope]. */
@DocxktDsl
public class FooterScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    private val children = mutableListOf<XmlComponent>()

    /** Add a paragraph to the footer. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val prev = context.inHeaderFooterScope
        context.inHeaderFooterScope = true
        context.pushImageOwner(DocumentContext.ImageOwner.Footer(this))
        try {
            val scope = ParagraphScope(context)
            scope.configure()
            children += scope.build()
        } finally {
            context.popImageOwner()
            context.inHeaderFooterScope = prev
        }
    }

    /** Add a table to the footer. */
    public fun table(configure: TableScope.() -> Unit) {
        val prev = context.inHeaderFooterScope
        context.inHeaderFooterScope = true
        context.pushImageOwner(DocumentContext.ImageOwner.Footer(this))
        try {
            val scope = TableScope(context)
            scope.configure()
            children += scope.build()
        } finally {
            context.popImageOwner()
            context.inHeaderFooterScope = prev
        }
    }

    internal fun build(): Footer {
        val footer = Footer(children = children.toList())
        context.bindFooterScope(footer, this)
        return footer
    }
}
