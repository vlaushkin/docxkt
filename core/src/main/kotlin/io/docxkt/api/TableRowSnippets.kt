// No upstream analogue — Kotlin idiom for the patcher cross-module
// boundary. Mirrors ParagraphSnippets but for table rows.
package io.docxkt.api

import io.docxkt.dsl.DocxktDsl
import io.docxkt.dsl.TableRowScope
import io.docxkt.model.table.TableRow

/**
 * Standalone table rows for use as input to the patcher's
 * `Patch.Rows` patch type. Built via the [tableRows] top-level
 * DSL.
 *
 * The underlying [TableRow] type is `:core`-internal; this wrapper
 * is the only public surface that crosses the module boundary
 * into `:patcher`.
 *
 * [toXml] returns one `<w:tr>…</w:tr>` string per row, in source
 * order. The `:patcher` module wraps each in a minimal envelope
 * to import as a DOM node.
 */
public class TableRowSnippets internal constructor(
    internal val rows: List<TableRow>,
) {
    public fun toXml(): List<String> = rows.map { r ->
        StringBuilder().apply { r.appendXml(this) }.toString()
    }

    public val size: Int get() = rows.size
}

/**
 * Top-level DSL — build a list of standalone table rows without
 * the surrounding `<w:tbl>` context.
 *
 * ```
 * val rows = tableRows {
 *     row { cell { paragraph { text("Alice") } } }
 *     row { cell { paragraph { text("Bob") } } }
 * }
 * ```
 *
 * The rows share a fresh DSL state — they cannot register lists,
 * hyperlinks, or images.
 */
public fun tableRows(configure: TableRowSnippetsScope.() -> Unit): TableRowSnippets {
    val scope = TableRowSnippetsScope()
    scope.configure()
    return TableRowSnippets(scope.build())
}

@DocxktDsl
public class TableRowSnippetsScope internal constructor() {
    private val rows = mutableListOf<TableRow>()

    public fun row(configure: TableRowScope.() -> Unit) {
        val scope = TableRowScope()
        scope.configure()
        rows += scope.build()
    }

    internal fun build(): List<TableRow> = rows.toList()
}
