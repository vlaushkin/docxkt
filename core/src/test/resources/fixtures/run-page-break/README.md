# Fixture: run-page-break

**What this demonstrates:** `<w:br w:type="page"/>` inside its own
`<w:r>` — upstream's `PageBreak` class emits a break-only run with
the page-break attribute. Locks `BreakType.PAGE` → wire
`w:type="page"`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ children: [new TextRun("before"), new PageBreak(), new TextRun("after")] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
