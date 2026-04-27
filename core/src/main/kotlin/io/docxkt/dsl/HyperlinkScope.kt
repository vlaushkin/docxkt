// No upstream analogue — DSL scope receivers are a Kotlin idiom;
// upstream builds ExternalHyperlink via `new ExternalHyperlink({ link,
// children })`.
package io.docxkt.dsl

import io.docxkt.model.paragraph.run.Break
import io.docxkt.model.paragraph.run.BreakType
import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text

/**
 * Builder for the runs wrapped inside a `<w:hyperlink>`. Exposes only
 * run-producing methods — no paragraph-level properties, no nested
 * hyperlinks (OOXML rejects nesting).
 *
 * The scope reuses [RunScope] for inline run formatting so the
 * hyperlink's runs can carry bold / italics / color / `styleReference`
 * etc. A call like
 *
 * ```
 * hyperlink("https://example.com") {
 *     text("Example") { styleReference = "Hyperlink" }
 * }
 * ```
 *
 * produces one `<w:r>` with an `<w:rStyle w:val="Hyperlink"/>` inside
 * the `<w:hyperlink>`.
 */
@DocxktDsl
public class HyperlinkScope internal constructor(
    internal val context: DocumentContext,
) {
    private val runs = mutableListOf<Run>()

    /** Add a plain-text run. */
    public fun text(value: String) {
        runs += Run(children = listOf(Text(value)))
    }

    /**
     * Add a text run with run-level formatting via [configure]. Same
     * semantics as `ParagraphScope.text(value, configure)`.
     */
    public fun text(value: String, configure: RunScope.() -> Unit) {
        val scope = RunScope(context)
        scope.configure()
        runs += Run(
            children = scope.leadingChildren() +
                listOf<io.docxkt.xml.XmlComponent>(Text(value)) +
                scope.extraChildren(),
            properties = scope.buildProperties(),
        )
    }

    /** Soft line break inside the hyperlink. Rare but valid. */
    public fun lineBreak() {
        runs += Run(children = listOf(Break(BreakType.LINE)))
    }

    internal fun buildRuns(): List<Run> = runs.toList()
}
