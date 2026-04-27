// Port of: src/patcher/from-docx.ts (`patchDocument` parameter
//          shape — keepOriginalStyles, placeholderDelimiters,
//          recursive). Internal — patcher-wide options bundle
//          threaded through every injector.
package io.docxkt.patcher.replace

/**
 * Configuration shared across [TokenReplacer], [ParagraphInjector],
 * [ImageInjector], and [RowInjector]. Constructed once at the top of
 * [io.docxkt.patcher.PatchDocument.patch] from the public `patch()`
 * parameters.
 *
 * - [keepOriginalStyles]: when true (default), replacement content
 *   inherits the source run's `<w:rPr>`. When false, replacement
 *   content lands in a fresh bare `<w:r>`.
 * - [placeholderStart] / [placeholderEnd]: marker delimiters,
 *   default `{{` / `}}`.
 * - [recursive]: when true (default), the replacer re-scans after
 *   each substitution so a replacement value containing another
 *   `{{key}}` resolves transitively. When false, each key is
 *   replaced at most once per `patch()` call.
 */
internal data class PatchOptions(
    val keepOriginalStyles: Boolean = true,
    val placeholderStart: String = "{{",
    val placeholderEnd: String = "}}",
    val recursive: Boolean = true,
) {
    /**
     * Build the regex that matches `<start>(key)<end>`. Group 1 is
     * the key. Delimiters are `Regex.escape`d; the captured key has
     * no inner-character constraint other than non-greediness, which
     * matches upstream's permissive behaviour.
     */
    fun buildMarkerRegex(): Regex =
        Regex(Regex.escape(placeholderStart) + "(.+?)" + Regex.escape(placeholderEnd))
}
