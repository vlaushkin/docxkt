# Fixture: demo-22-right-to-left-text

Phase 38 port of `/opt/docx-ref/demo/22-right-to-left-text.ts`.

Three Hebrew paragraphs with bidirectional + rightToLeft flags
(plain, bold, italic), followed by a 2×2 table that flips visually
RTL via `<w:bidiVisual/>`. Closes Phase 37b "bidiVisual on Table"
gap.

**Compared XML parts:** `word/document.xml`
