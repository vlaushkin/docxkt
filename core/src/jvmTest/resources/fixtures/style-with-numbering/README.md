# Fixture: style-with-numbering

**What this demonstrates:** Phase 11 stretch fixture. Combines
Phase 10 (numbering) with Phase 11 (styles). A paragraph style
`BulletItem` (basedOn `Normal`, italic rPr) plus a bullet list
template; two paragraphs reference both.

**Key interaction:** when a paragraph has both an explicit
`styleReference` and a `numbering` reference, the explicit
style wins — the auto-`ListParagraph` promotion from Phase 10 is
suppressed. Emitted order inside each `<w:pPr>` is
`<w:pStyle>` → `<w:numPr>`.

- `word/document.xml` — two paragraphs, each with
  `<w:pPr><w:pStyle w:val="BulletItem"/><w:numPr><w:ilvl/><w:numId/>
  </w:numPr></w:pPr>`.
- `word/styles.xml` — one `<w:style w:styleId="BulletItem">`
  with `<w:name>`, `<w:basedOn>`, `<w:rPr>` (italics).
- `word/numbering.xml` — one abstract with a bullet level plus
  one concrete.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ numbering: {config}, styles: {paragraphStyles}, ... })`
- `new Paragraph({ style, numbering, children })` × 2

**Compared XML parts:**
- `word/document.xml`
- `word/styles.xml`
- `word/numbering.xml`

**Modifications from raw upstream extraction:**
- `<w:docDefaults>` stripped.
- All factory-shipped `<w:style>` entries stripped (only
  `BulletItem` kept).
- Upstream's default-bullet `<w:abstractNum abstractNumId="1">`
  stripped and user abstract/concrete renumbered to 1
  (standard Phase 10 publisher normalization).

**Publisher invocation:**
```
publish.mjs --keep-styles BulletItem style-with-numbering word/styles.xml word/numbering.xml
```
