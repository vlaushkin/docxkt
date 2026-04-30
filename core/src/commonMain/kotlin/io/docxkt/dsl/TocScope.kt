// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.toc.TableOfContents
import io.docxkt.model.toc.TocOptions

/**
 * Configure a table-of-contents block (`<w:sdt>` containing
 * the TOC complex-field chain). Exposes the four most
 * commonly-used Word TOC switches.
 *
 * Defaults match upstream's minimal-TOC defaults: no
 * hyperlink, no headingStyleRange (Word defaults to 1-3 when
 * absent), no useAppliedParagraphOutlineLevel, no
 * hideTabAndPageNumbersInWebView.
 */
@DocxktDsl
public class TocScope internal constructor() {
    public var alias: String? = "Table of Contents"

    // The three TOC flags below are TS-side switches in the field
    // instr text (`\h`, `\u`, `\w`) — emit-true-or-omit. Setting
    // false is byte-identical to leaving the default; there's no
    // "inherit" state on the OOXML wire. Kept as plain `Boolean`.
    public var hyperlink: Boolean = false

    /** Range string like `"1-3"`; maps to the `\o` switch. */
    public var headingStyleRange: String? = null

    public var useAppliedParagraphOutlineLevel: Boolean = false
    public var hideTabAndPageNumbersInWebView: Boolean = false

    internal fun build(): TableOfContents = TableOfContents(
        alias = alias,
        options = TocOptions(
            hyperlink = hyperlink,
            headingStyleRange = headingStyleRange,
            useAppliedParagraphOutlineLevel = useAppliedParagraphOutlineLevel,
            hideTabAndPageNumbersInWebView = hideTabAndPageNumbersInWebView,
        ),
    )
}
