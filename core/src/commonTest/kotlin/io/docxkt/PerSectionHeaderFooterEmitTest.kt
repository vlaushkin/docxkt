// No upstream analogue — programmatic emit test for the per-section
// header/footer surface introduced in v1.2.0. Targets the "different
// pgSz per page" use case (each image gets its own section so the page
// can be shrunk to fit) and proves that:
//   - each inline <w:sectPr> carries headerReference + footerReference;
//   - each unique header/footer gets a dedicated headerN/footerN part;
//   - [Content_Types].xml lists an Override per emitted H/F part;
//   - word/_rels/document.xml.rels carries a Relationship per H/F.
package io.docxkt

import io.docxkt.api.document
import io.docxkt.api.toByteArray
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.pack.ZipReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PerSectionHeaderFooterEmitTest {

    @Test
    fun `two inline sections plus trailing — each carries its own H and F parts`() {
        val bytes = document {
            // Section 1 — page A, header/footer A.
            paragraph { text("image one") }
            sectionBreak {
                pageSize(widthTwips = 6000, heightTwips = 8000)
                margins(top = 200, bottom = 200, left = 200, right = 200)
                header(type = HeaderFooterReferenceType.DEFAULT) {
                    paragraph { text("HEADER A") }
                }
                footer(type = HeaderFooterReferenceType.DEFAULT) {
                    paragraph { text("FOOTER A") }
                }
            }

            // Section 2 — page B (taller), header/footer B.
            paragraph { text("image two") }
            sectionBreak {
                pageSize(widthTwips = 6000, heightTwips = 9000)
                margins(top = 200, bottom = 200, left = 200, right = 200)
                header(type = HeaderFooterReferenceType.DEFAULT) {
                    paragraph { text("HEADER B") }
                }
                footer(type = HeaderFooterReferenceType.DEFAULT) {
                    paragraph { text("FOOTER B") }
                }
            }

            // Trailing section — uses document-level H/F slots.
            paragraph { text("image three") }
            header(type = HeaderFooterReferenceType.DEFAULT) {
                paragraph { text("HEADER C") }
            }
            footer(type = HeaderFooterReferenceType.DEFAULT) {
                paragraph { text("FOOTER C") }
            }
        }.toByteArray()

        val parts = ZipReader.read(bytes)

        val docXml = parts["word/document.xml"]?.decodeToString()
        assertNotNull(docXml, "word/document.xml missing from emitted ZIP")

        // 2 inline sectPr (inside paragraphs, wrapped by <w:pPr>) +
        // 1 trailing sectPr (direct child of <w:body>). Match the
        // opening tag without the `/>` so self-closing variants don't
        // double-count.
        val sectPrOpens = Regex("<w:sectPr\\b").findAll(docXml).count()
        assertEquals(3, sectPrOpens, "Expected 3 <w:sectPr> (2 inline + 1 trailing), got $sectPrOpens")

        // Each section must reference a header AND a footer.
        val headerRefs = Regex("<w:headerReference\\b").findAll(docXml).count()
        val footerRefs = Regex("<w:footerReference\\b").findAll(docXml).count()
        assertEquals(3, headerRefs, "Expected 3 <w:headerReference> in document.xml, got $headerRefs")
        assertEquals(3, footerRefs, "Expected 3 <w:footerReference> in document.xml, got $footerRefs")

        // Three unique headers + three unique footers (no dedupe).
        for (i in 1..3) {
            val h = parts["word/header$i.xml"]
            assertNotNull(h, "word/header$i.xml missing")
            val f = parts["word/footer$i.xml"]
            assertNotNull(f, "word/footer$i.xml missing")
        }
        assertTrue(
            parts["word/header4.xml"] == null,
            "Unexpected fourth header part — dedupe is off, count should be 3",
        )

        // Per-section content is wired to its own part. Order of part
        // allocation walks mid-body sections first, then the trailing
        // section, so:
        //   header1 = "HEADER A", header2 = "HEADER B", header3 = "HEADER C"
        // Same for footers. Verify by content.
        val h1 = parts["word/header1.xml"]!!.decodeToString()
        val h2 = parts["word/header2.xml"]!!.decodeToString()
        val h3 = parts["word/header3.xml"]!!.decodeToString()
        assertTrue("HEADER A" in h1, "header1 should carry 'HEADER A'; got: $h1")
        assertTrue("HEADER B" in h2, "header2 should carry 'HEADER B'; got: $h2")
        assertTrue("HEADER C" in h3, "header3 should carry 'HEADER C'; got: $h3")

        val f1 = parts["word/footer1.xml"]!!.decodeToString()
        val f2 = parts["word/footer2.xml"]!!.decodeToString()
        val f3 = parts["word/footer3.xml"]!!.decodeToString()
        assertTrue("FOOTER A" in f1, "footer1 should carry 'FOOTER A'; got: $f1")
        assertTrue("FOOTER B" in f2, "footer2 should carry 'FOOTER B'; got: $f2")
        assertTrue("FOOTER C" in f3, "footer3 should carry 'FOOTER C'; got: $f3")

        // [Content_Types].xml — one Override per emitted H/F part.
        val ct = parts["[Content_Types].xml"]?.decodeToString()
        assertNotNull(ct, "[Content_Types].xml missing")
        for (i in 1..3) {
            assertTrue(
                "/word/header$i.xml" in ct,
                "[Content_Types].xml missing Override for /word/header$i.xml; got: $ct",
            )
            assertTrue(
                "/word/footer$i.xml" in ct,
                "[Content_Types].xml missing Override for /word/footer$i.xml; got: $ct",
            )
        }

        // word/_rels/document.xml.rels — one Relationship per H/F.
        val rels = parts["word/_rels/document.xml.rels"]?.decodeToString()
        assertNotNull(rels, "word/_rels/document.xml.rels missing")
        for (i in 1..3) {
            assertTrue(
                "Target=\"header$i.xml\"" in rels,
                "document.xml.rels missing header$i.xml Target; got: $rels",
            )
            assertTrue(
                "Target=\"footer$i.xml\"" in rels,
                "document.xml.rels missing footer$i.xml Target; got: $rels",
            )
        }

        // Each inline sectPr (the two before the trailing one) must
        // sit inside a <w:pPr>. The trailing sectPr is a direct child
        // of <w:body>. Verify by looking at substring positions.
        val pPrSectPr = Regex("<w:pPr>[\\s\\S]*?<w:sectPr\\b").findAll(docXml).count()
        assertEquals(2, pPrSectPr, "Expected 2 inline sectPr (inside <w:pPr>), got $pPrSectPr")
    }

    @Test
    fun `inline section's titlePage + FIRST header attach to that section only`() {
        val bytes = document {
            paragraph { text("first section body") }
            sectionBreak {
                titlePage()
                header(type = HeaderFooterReferenceType.FIRST) {
                    paragraph { text("INLINE TITLE") }
                }
                header(type = HeaderFooterReferenceType.DEFAULT) {
                    paragraph { text("INLINE DEFAULT") }
                }
            }
            paragraph { text("second section body") }
        }.toByteArray()

        val parts = ZipReader.read(bytes)
        val docXml = parts["word/document.xml"]!!.decodeToString()

        // The inline sectPr (first one in source order) is the one that
        // carries <w:titlePg/>. The trailing sectPr does not.
        val firstSectPrStart = docXml.indexOf("<w:sectPr")
        val firstSectPrEnd = docXml.indexOf("</w:sectPr>", firstSectPrStart)
        val firstSectPr = docXml.substring(firstSectPrStart, firstSectPrEnd)
        assertTrue(
            "<w:titlePg/>" in firstSectPr,
            "Inline sectPr should carry <w:titlePg/>; got: $firstSectPr",
        )

        val secondSectPrStart = docXml.indexOf("<w:sectPr", firstSectPrEnd)
        val secondSectPrEnd = docXml.indexOf("</w:sectPr>", secondSectPrStart)
        val secondSectPr = docXml.substring(secondSectPrStart, secondSectPrEnd)
        assertTrue(
            "<w:titlePg/>" !in secondSectPr,
            "Trailing sectPr should NOT carry <w:titlePg/>; got: $secondSectPr",
        )

        // The inline sectPr references TWO headers (DEFAULT + FIRST).
        val inlineDefaultRef = "w:type=\"default\"" in firstSectPr
        val inlineFirstRef = "w:type=\"first\"" in firstSectPr
        assertTrue(inlineDefaultRef, "Inline sectPr missing default headerReference")
        assertTrue(inlineFirstRef, "Inline sectPr missing first headerReference")
    }
}
