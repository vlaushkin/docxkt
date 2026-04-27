// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text

/**
 * Builder for the runs wrapped inside a `<w:ins>` or `<w:del>`
 * revision marker. Exposes only the text-run construction
 * surface — no block-level properties, no nested revisions.
 *
 * Matches upstream's `InsertedTextRun` / `DeletedTextRun`
 * constructor shape (which wraps a single `TextRun`-style
 * options bag); we generalize to "a list of Run children"
 * because multi-run revisions are admissible per OOXML.
 */
@DocxktDsl
public class RevisionRunsScope internal constructor(
    internal val context: DocumentContext,
) {
    private val runs = mutableListOf<Run>()

    /** Add a plain-text run. */
    public fun text(value: String) {
        runs += Run(children = listOf(Text(value)))
    }

    /**
     * Add a text run with run-level formatting configured via
     * [configure]. Same semantics as
     * [ParagraphScope.text] with a lambda.
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

    internal fun buildRuns(): List<Run> = runs.toList()
}
