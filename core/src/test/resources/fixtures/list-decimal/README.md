# Fixture: list-decimal

**What this demonstrates:** first Phase 10 fixture. A document with
one decimal list template (three levels) and three paragraphs
referencing levels 0 / 1 / 2.

- `word/document.xml` — three paragraphs each with
  `<w:numPr><w:ilvl/><w:numId w:val="1"/></w:numPr>` inside
  `<w:pPr>`. `w:ilvl` changes per paragraph.
- `word/numbering.xml` — one `<w:abstractNum w:abstractNumId="1">`
  with three `<w:lvl>` children (each carrying
  `w15:tentative="1"`) plus one `<w:num w:numId="1">` pointing at
  it.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ numbering: { config: [{ reference, levels }] }, ... })`
- `new Paragraph({ numbering: { reference, level } })` × 3

**Compared XML parts:**
- `word/document.xml`
- `word/numbering.xml`

**Modifications from raw upstream extraction:**
- Upstream's auto-emitted default-bullet
  `<w:abstractNum w:abstractNumId="1">` stripped — we don't ship
  the fallback block.
- `<w:abstractNum w:abstractNumId="2">` (user template) renumbered
  to `abstractNumId="1"` + `<w:num w:numId="2">` renumbered to
  `numId="1"` + `<w:numPr><w:numId w:val="2"/>` inside
  `word/document.xml` renumbered to `w:val="1"` — matches our
  allocator's sequential scheme.
