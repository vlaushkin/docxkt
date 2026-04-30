# Fixture: paragraph-alignment

**What this demonstrates:** `<w:jc w:val="center"/>` and
`<w:jc w:val="right"/>` across two paragraphs. Exercises
`AlignmentType` enum → wire mapping and `<w:jc>`'s position inside
`<w:pPr>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ alignment: AlignmentType.CENTER, children: [...] })`
- `new Paragraph({ alignment: AlignmentType.RIGHT, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
