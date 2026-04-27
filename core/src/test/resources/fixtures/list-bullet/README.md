# Fixture: list-bullet

**What this demonstrates:** second Phase 10 fixture. A bullet list
template with two levels (filled "●" / hollow "○") and two
paragraphs at levels 0 / 1.

- `word/document.xml` — two paragraphs each with
  `<w:numPr><w:ilvl/><w:numId w:val="1"/></w:numPr>` inside
  `<w:pPr>`. First paragraph `ilvl=0`, second `ilvl=1`.
- `word/numbering.xml` — one `<w:abstractNum w:abstractNumId="1">`
  with two `<w:lvl>` children using `w:numFmt w:val="bullet"`
  and the Unicode bullet glyphs as `w:lvlText`. One
  `<w:num w:numId="1">` pointing at it.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ numbering: { config: [{ reference, levels }] }, ... })`
  with `LevelFormat.BULLET`.
- `new Paragraph({ numbering: { reference, level } })` × 2

**Compared XML parts:**
- `word/document.xml`
- `word/numbering.xml`

**Modifications from raw upstream extraction:**
- Upstream's auto-emitted default-bullet
  `<w:abstractNum w:abstractNumId="1">` stripped — we don't ship
  the fallback block.
- User template's `abstractNumId="2"` / `numId="2"` renumbered to
  `1` (and the matching `<w:numId w:val>` inside
  `word/document.xml`) to match our 1-based allocator.
