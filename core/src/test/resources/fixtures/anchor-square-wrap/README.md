# Fixture: anchor-square-wrap

**What this demonstrates:** first Phase 22 fixture. A floating
PNG image positioned with column-relative horizontal and
paragraph-relative vertical offsets (both 0) and square wrap
on both sides — the most common floating-image case.

- `word/document.xml` — `<w:r><w:drawing><wp:anchor>` with the
  full child sequence: simplePos, positionH (with `<wp:posOffset>0</wp:posOffset>`),
  positionV (same), extent, effectExtent, wrapSquare, docPr,
  cNvGraphicFramePr, graphic.
- `word/media/image1.png` — same 2×2 PNG payload Phase 7
  fixtures use.

**Compared XML parts:**
- `word/document.xml`
- `word/media/image1.png` (binary)
