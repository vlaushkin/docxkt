# Fixture: list-lower-roman

**What this demonstrates:** third Phase 10 fixture. A
single-level lower-roman list (`i.`, `ii.`, `iii.` …) with three
paragraphs at level 0.

- `word/document.xml` — three paragraphs each with
  `<w:numPr><w:ilvl w:val="0"/><w:numId w:val="1"/></w:numPr>`.
- `word/numbering.xml` — one `<w:abstractNum w:abstractNumId="1">`
  with a single `<w:lvl>` child carrying
  `<w:numFmt w:val="lowerRoman"/>` and `<w:lvlText w:val="%1."/>`.
  One `<w:num w:numId="1">` pointing at it.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ numbering: { config: [{ reference, levels }] }, ... })`
  with `LevelFormat.LOWER_ROMAN`.
- `new Paragraph({ numbering: { reference, level } })` × 3

**Compared XML parts:**
- `word/document.xml`
- `word/numbering.xml`

**Modifications from raw upstream extraction:**
- Upstream's auto-emitted default-bullet
  `<w:abstractNum w:abstractNumId="1">` stripped.
- User template's `abstractNumId="2"` / `numId="2"` renumbered to
  `1` + matching `<w:numId w:val>` inside `word/document.xml`
  renumbered to `1` — matches our allocator's 1-based scheme.
