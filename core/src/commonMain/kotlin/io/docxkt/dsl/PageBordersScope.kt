// No upstream analogue — DSL scope receiver for `<w:pgBorders>`. Upstream
// constructs PageBorders via a typed options object; we expose a small
// scope here.
package io.docxkt.dsl

import io.docxkt.model.border.BorderSide
import io.docxkt.model.section.PageBorderDisplay
import io.docxkt.model.section.PageBorderOffsetFrom
import io.docxkt.model.section.PageBorderZOrder

/**
 * Builder for `<w:pgBorders>`. Sides are property setters (assign a
 * [BorderSide]) — the existing [BorderSidesScope] doesn't fit because
 * page-borders has only the four cardinal sides plus three top-level
 * attributes, and emits sides in `top → left → bottom → right` (XSD)
 * order rather than the `top → bottom → left → right` order used by
 * paragraph borders.
 */
@DocxktDsl
public class PageBordersScope internal constructor() {
    public var top: BorderSide? = null
    public var left: BorderSide? = null
    public var bottom: BorderSide? = null
    public var right: BorderSide? = null

    /**
     * Which pages display the page border (`allPages` / `firstPage`
     * / `notFirstPage`). Null suppresses the attribute.
     */
    public var display: PageBorderDisplay? = null

    /**
     * Whether the border is positioned relative to the page edge
     * (`page`) or text (`text`). Null suppresses.
     */
    public var offsetFrom: PageBorderOffsetFrom? = null

    /**
     * Whether the border draws in front of (`front`) or behind
     * (`back`) page contents. Null suppresses.
     */
    public var zOrder: PageBorderZOrder? = null
}
