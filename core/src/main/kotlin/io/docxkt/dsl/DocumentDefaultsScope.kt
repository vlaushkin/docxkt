// No upstream analogue — DSL scope receiver for <w:docDefaults>.
package io.docxkt.dsl

import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.run.RunProperties

/**
 * Configures `<w:docDefaults>` — document-wide default formatting
 * applied to every paragraph and run unless overridden. Mirrors
 * upstream's `styles.default.{run,paragraph}` block.
 */
@DocxktDsl
public class DocumentDefaultsScope internal constructor() {
    internal var runProps: RunProperties? = null
    internal var paragraphProps: ParagraphProperties? = null

    /** Configure the default `<w:rPr>` applied to every run. */
    public fun run(configure: RunScope.() -> Unit) {
        val scope = RunScope().apply(configure)
        runProps = scope.buildProperties()
    }

    /** Configure the default `<w:pPr>` applied to every paragraph. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope().apply(configure)
        paragraphProps = scope.buildProperties()
    }
}
