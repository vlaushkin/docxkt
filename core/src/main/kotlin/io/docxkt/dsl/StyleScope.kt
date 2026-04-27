// No upstream analogue — DSL scope receivers are a Kotlin idiom; upstream
// configures styles via `IParagraphStyleOptions` / `ICharacterStyleOptions`
// object literals.
package io.docxkt.dsl

import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.run.RunProperties
import io.docxkt.model.style.Style
import io.docxkt.model.style.StyleType

/**
 * Configures a single paragraph or character style registered via
 * [DocumentScope.paragraphStyle] or [DocumentScope.characterStyle].
 *
 * Setter semantics mirror the [Style] model: `null` means "not
 * emitted", any non-null value causes the corresponding `<w:*>` child
 * to be emitted. `paragraph { … }` and `run { … }` blocks reuse the
 * existing [ParagraphScope] / [RunScope] property surfaces — the
 * blocks build a [ParagraphProperties] / [RunProperties] directly,
 * discarding any runs or block-level additions the scopes
 * incidentally allow.
 *
 * Character styles ignore any `paragraph { … }` block. OOXML does
 * not permit a `<w:pPr>` child inside
 * `<w:style w:type="character">`; [buildCharacterStyle] drops the
 * ParagraphProperties unconditionally.
 */
@DocxktDsl
public class StyleScope internal constructor() {
    private var nameValue: String? = null
    private var basedOnValue: String? = null
    private var nextValue: String? = null
    private var linkValue: String? = null
    private var uiPriorityValue: Int? = null
    private var semiHiddenValue: Boolean? = null
    private var unhideWhenUsedValue: Boolean? = null
    private var quickFormatValue: Boolean? = null
    private var paragraphProperties: ParagraphProperties? = null
    private var runProperties: RunProperties? = null

    /** Display name (`<w:name w:val="…"/>`). */
    public fun name(value: String) { nameValue = value }

    /** Parent style id (`<w:basedOn w:val="…"/>`). */
    public fun basedOn(value: String) { basedOnValue = value }

    /**
     * Style id to auto-apply to the following paragraph
     * (`<w:next w:val="…"/>`). Paragraph styles only; ignored for
     * character styles.
     */
    public fun next(value: String) { nextValue = value }

    /** Linked paragraph/character pair style id (`<w:link w:val="…"/>`). */
    public fun link(value: String) { linkValue = value }

    /**
     * UI priority (`<w:uiPriority w:val="N"/>`). Lower numbers appear
     * first in style pickers.
     */
    public fun uiPriority(value: Int) { uiPriorityValue = value }

    /** Semi-hidden flag (`<w:semiHidden/>`). */
    public fun semiHidden(value: Boolean) { semiHiddenValue = value }

    /** Unhide-when-used flag (`<w:unhideWhenUsed/>`). */
    public fun unhideWhenUsed(value: Boolean) { unhideWhenUsedValue = value }

    /** Quick-format flag (`<w:qFormat/>`). */
    public fun quickFormat(value: Boolean) { quickFormatValue = value }

    /**
     * Configure the style's paragraph-level properties. Reuses
     * [ParagraphScope]'s property surface; runs added inside are
     * silently ignored. A block that touches no properties produces
     * no `<w:pPr>` in the emitted style (IgnoreIfEmpty).
     */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope()
        scope.configure()
        paragraphProperties = scope.buildProperties()
    }

    /**
     * Configure the style's run-level properties. Reuses [RunScope]'s
     * property surface; children like `lineBreak()` / `image()` added
     * inside are ignored. A block that touches no properties
     * produces no `<w:rPr>` in the emitted style (IgnoreIfEmpty).
     */
    public fun run(configure: RunScope.() -> Unit) {
        val scope = RunScope()
        scope.configure()
        runProperties = scope.buildProperties()
    }

    internal fun buildParagraphStyle(id: String): Style = Style(
        type = StyleType.PARAGRAPH,
        id = id,
        name = nameValue,
        basedOn = basedOnValue,
        next = nextValue,
        link = linkValue,
        uiPriority = uiPriorityValue,
        semiHidden = semiHiddenValue,
        unhideWhenUsed = unhideWhenUsedValue,
        quickFormat = quickFormatValue,
        paragraphProperties = paragraphProperties,
        runProperties = runProperties,
    )

    internal fun buildCharacterStyle(id: String): Style = Style(
        type = StyleType.CHARACTER,
        id = id,
        name = nameValue,
        basedOn = basedOnValue,
        // Character styles can't carry a `<w:next>` (OOXML rejects the
        // pairing; upstream's StyleForCharacter doesn't expose the
        // option). We drop the value here rather than error, matching
        // the Kotlin idiom of "ignore inapplicable flags".
        next = null,
        link = linkValue,
        // Upstream's StyleForCharacter constructor auto-applies
        // `uiPriority: 99` and `unhideWhenUsed: true` when the user
        // hasn't explicitly set them. Match byte-for-byte — without
        // these defaults our character-style fixtures diff red
        // against upstream's emitted wire.
        uiPriority = uiPriorityValue ?: 99,
        semiHidden = semiHiddenValue,
        unhideWhenUsed = unhideWhenUsedValue ?: true,
        quickFormat = quickFormatValue,
        paragraphProperties = null,
        runProperties = runProperties,
    )
}
