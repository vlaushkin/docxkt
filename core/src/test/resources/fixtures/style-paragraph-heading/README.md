# Fixture: style-paragraph-heading

**What this demonstrates:** first Phase 11 fixture. One
user-declared paragraph style (`Heading1`, basedOn `Normal`, bold
run-level formatting at 32 half-points = 16 pt); one paragraph
referencing it via `<w:pStyle w:val="Heading1"/>`.

- `word/document.xml` — single paragraph with
  `<w:pPr><w:pStyle w:val="Heading1"/></w:pPr>` and a text run.
- `word/styles.xml` — one `<w:style w:type="paragraph"
  w:styleId="Heading1">` with `<w:name>`, `<w:basedOn>`, and
  `<w:rPr>` carrying `<w:b/>`, `<w:bCs/>`, `<w:sz w:val="32"/>`,
  `<w:szCs w:val="32"/>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ styles: { paragraphStyles: [{ id, name, basedOn,
  run }] }, ... })`
- `new Paragraph({ style: "Heading1", children: [new TextRun(...)] })`

**Compared XML parts:**
- `word/document.xml`
- `word/styles.xml`

**Modifications from raw upstream extraction:**
- Upstream's `<w:docDefaults>` (empty rPrDefault + pPrDefault)
  block stripped — we don't emit docDefaults at the model layer.
- Upstream's ~15 factory-shipped `<w:style>` entries (Title,
  Heading1–6, Strong, ListParagraph, Hyperlink, FootnoteReference
  etc.) stripped. Only the user-declared `Heading1` survives.
  When the factory's Heading1 collides with the user's Heading1,
  the publisher keeps the last occurrence (user's), matching
  DSL-declaration-order semantics.

**Publisher invocation:**
```
publish.mjs --keep-styles Heading1 style-paragraph-heading word/styles.xml
```
