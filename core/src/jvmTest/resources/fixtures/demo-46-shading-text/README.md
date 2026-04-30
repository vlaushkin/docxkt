# Fixture: demo-46-shading-text

Phase 40 port of `/opt/docx-ref/demo/46-shading-text.ts`.

A document with one default header (rId7 in the sectPr) and a
single body paragraph carrying two embossed/imprinted runs.
The header itself contains complex shading + Garamond-fonted
text — but only `word/document.xml` is compared. Re-port enabled
by Phase 40b's rId allocator refactor (header now naturally lands
at rId7).

**Compared XML parts:** `word/document.xml`
