// Port of: src/file/paragraph/run/image-run.ts (IImageOptions shape).
package io.docxkt.model.drawing

/**
 * An inline image: raw bytes + pixel-dimension-in-EMUs + format +
 * optional alt-text. Callers hand bytes in; `:core` does no I/O and
 * no image decoding.
 *
 * **EMU conversions are the caller's responsibility.** One inch is
 * 914,400 EMUs; at 96 DPI, one pixel is 9,525 EMUs. For a 100x100
 * pixel image at 96 DPI the caller passes
 * `widthEmus = 952500, heightEmus = 952500`.
 *
 * [description] is the `descr` attribute on `<wp:docPr>` — the
 * alt-text Word screen-readers announce. `null` emits empty
 * `descr=""` matching upstream's default.
 */
internal data class Image(
    val bytes: ByteArray,
    val widthEmus: Int,
    val heightEmus: Int,
    val format: ImageFormat,
    val description: String? = null,
) {
    // Override equals/hashCode so two Image instances with identical
    // bytes compare equal — tests and debugging benefit.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false
        return widthEmus == other.widthEmus &&
            heightEmus == other.heightEmus &&
            format == other.format &&
            description == other.description &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = widthEmus
        result = 31 * result + heightEmus
        result = 31 * result + format.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
