package io.docxkt.patcher

import io.docxkt.model.drawing.ImageFormat
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherImageReplaceTest : PatcherFixtureTest("patcher-image-replace") {

    private val tinyPng: ByteArray = byteArrayOf(
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
        0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4.toByte(),
        0x89.toByte(), 0x00, 0x00, 0x00, 0x0D, 0x49, 0x44, 0x41,
        0x54, 0x78, 0x9C.toByte(), 0x62, 0x00, 0x01, 0x00, 0x00,
        0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, 0xB4.toByte(), 0x00,
        0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(),
        0x42, 0x60, 0x82.toByte(),
    )

    override fun patches(): Map<String, Patch> = mapOf(
        "logo" to Patch.Image(
            bytes = tinyPng,
            widthEmus = 952500,
            heightEmus = 952500,
            format = ImageFormat.PNG,
        ),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "[Content_Types].xml",
        "word/_rels/document.xml.rels",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.png",
    )

    // Input parts: the document, content_types, both rels files.
    // Image binary is NOT in the input — the patcher creates it.
    override val inputParts: List<String> = listOf(
        "word/document.xml",
        "[Content_Types].xml",
        "_rels/.rels",
        "word/_rels/document.xml.rels",
    )
}
