// Port of: src/file/numbering/multi-level-type.ts.
package io.docxkt.model.numbering

/**
 * `<w:multiLevelType w:val="...">` inside `<w:abstractNum>`.
 *
 * Upstream always emits `HYBRID_MULTILEVEL` for user-defined lists
 * — we default the same.
 */
public enum class MultiLevelType(internal val wire: String) {
    SINGLE_LEVEL("singleLevel"),
    MULTILEVEL("multilevel"),
    HYBRID_MULTILEVEL("hybridMultilevel"),
}
