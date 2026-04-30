package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo23Base64ImagesTest : DocxFixtureTest("demo-23-base64-images") {

    private fun load(name: String): ByteArray =
        loadFixtureBytes("demo-23-base64-images", "$name")

    override fun build(): Document {
        val parrots = load("parrots.bmp")
        val img1 = load("image1.jpeg")
        val dog = load("dog.png")
        val cat = load("cat.jpg")
        val buffer = load("buffer.png")
        val emusPerPixel = 9525
        fun img(bytes: ByteArray, format: ImageFormat) = { run: io.docxkt.dsl.RunScope ->
            run.image(
                bytes = bytes,
                widthEmus = 100 * emusPerPixel,
                heightEmus = 100 * emusPerPixel,
                format = format,
            )
        }
        return document {
            paragraph {
                text("Hello World")
                text("") { img(parrots, ImageFormat.BMP)(this) }
            }
            paragraph { text("") { img(img1, ImageFormat.JPEG)(this) } }
            paragraph { text("") { img(dog, ImageFormat.PNG)(this) } }
            paragraph { text("") { img(cat, ImageFormat.JPEG)(this) } }
            paragraph { text("") { img(parrots, ImageFormat.BMP)(this) } }
            paragraph { text("") { img(buffer, ImageFormat.PNG)(this) } }
        }
    }
}
