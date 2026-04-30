// Port of: src/file/paragraph/run/image-run.ts (ImageType shape).
package io.docxkt.model.drawing

/**
 * Raster image format for inline images.
 *
 * The wire value is the file extension used in `word/media/image{N}.{ext}`
 * and the MIME type for `<Default>` entries in `[Content_Types].xml`.
 * SVG is not yet supported.
 */
public enum class ImageFormat(
    /** Extension suffix (no leading dot): `png`, `jpg`. */
    public val extension: String,
    /** MIME type (for `[Content_Types].xml`'s `<Default>` entry). */
    public val mimeType: String,
) {
    PNG(extension = "png", mimeType = "image/png"),
    JPEG(extension = "jpg", mimeType = "image/jpeg"),
    GIF(extension = "gif", mimeType = "image/gif"),
    BMP(extension = "bmp", mimeType = "image/bmp"),
}
