package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.table.VerticalAlignment
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class Demo50ReadmeDemoTest : DocxFixtureTest("demo-50-readme-demo") {

    override fun build(): Document {
        val img1 = loadFixtureBytes("demo-50-readme-demo", "image1.jpeg")
        val pizza = loadFixtureBytes("demo-50-readme-demo", "pizza.gif")
        val emusPerPixel = 9525
        return document {
            paragraph {
                styleReference = "Heading1"
                text("Hello World")
            }
            table {
                row {
                    cell {
                        verticalAlign(VerticalAlignment.CENTER)
                        paragraph {
                            text("") {
                                image(
                                    bytes = img1,
                                    widthEmus = 100 * emusPerPixel,
                                    heightEmus = 100 * emusPerPixel,
                                    format = ImageFormat.JPEG,
                                )
                            }
                        }
                    }
                    cell {
                        verticalAlign(VerticalAlignment.CENTER)
                        paragraph {
                            styleReference = "Heading1"
                            text("Hello")
                        }
                    }
                }
                row {
                    cell {
                        paragraph {
                            styleReference = "Heading1"
                            text("World")
                        }
                    }
                    cell {
                        paragraph {
                            text("") {
                                image(
                                    bytes = img1,
                                    widthEmus = 100 * emusPerPixel,
                                    heightEmus = 100 * emusPerPixel,
                                    format = ImageFormat.JPEG,
                                )
                            }
                        }
                    }
                }
            }
            paragraph {
                text("") {
                    image(
                        bytes = pizza,
                        widthEmus = 100 * emusPerPixel,
                        heightEmus = 100 * emusPerPixel,
                        format = ImageFormat.GIF,
                    )
                }
            }
        }
    }
}
