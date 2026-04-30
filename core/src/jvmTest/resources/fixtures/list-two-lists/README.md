# Fixture: list-two-lists

**What this demonstrates:** Phase 10 stretch fixture. Two
distinct list templates registered in the same document;
paragraphs reference each. Exercises the allocator's ability to
assign sequential `abstractNumId` / `numId` values across
multiple registrations and keep the `<w:numId w:val="…">`
pointers in `<w:numPr>` consistent per paragraph.

- `word/document.xml` — four paragraphs. First two reference
  `numId="1"` (decimal-list), last two reference `numId="2"`
  (bullet-list).
- `word/numbering.xml` — two `<w:abstractNum>` entries
  (`abstractNumId` 1 = decimal, 2 = bullet) and two matching
  `<w:num>` entries (numId 1 → abstractNumId 1, numId 2 →
  abstractNumId 2).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ numbering: { config: [ …two entries… ] }, ... })`
  with `LevelFormat.DECIMAL` and `LevelFormat.BULLET`.
- `new Paragraph({ numbering: { reference, level } })` × 4

**Compared XML parts:**
- `word/document.xml`
- `word/numbering.xml`

**Modifications from raw upstream extraction:**
- Upstream's auto-emitted default-bullet
  `<w:abstractNum w:abstractNumId="1">` stripped.
- User templates renumbered sequentially starting at `1`
  (upstream puts user configs at `abstractNumId=2,3,…`); the
  matching `<w:num>` and `<w:numPr w:numId>` references are
  renumbered to `1,2` — matches our allocator's scheme (order
  of `listTemplate` DSL calls = numId order).
