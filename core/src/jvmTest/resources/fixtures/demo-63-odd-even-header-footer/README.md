# Fixture: demo-63-odd-even-header-footer

Phase 40 port of `/opt/docx-ref/demo/63-odd-even-header-footer.ts`.

A document with `evenAndOddHeaderAndFooters: true` and four
header/footer references in the sectPr:
- header default → rId7
- header even → rId8
- footer default → rId9
- footer even → rId10

Body has 5 paragraphs each ending with a page break.
Re-port enabled by Phase 40b's rId allocator refactor — this
demo was the smoking gun for the "rId7+ vs rId1" mismatch.

**Compared XML parts:** `word/document.xml`
