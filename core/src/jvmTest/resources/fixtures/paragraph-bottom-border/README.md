# Fixture: paragraph-bottom-border

**What this demonstrates:** `<w:pPr><w:pBdr><w:bottom w:val="single"
w:color="auto" w:sz="6" w:space="1"/></w:pBdr></w:pPr>` — the OOXML
shape that renders as a markdown `ThematicBreak` (horizontal rule).

Locks (1) `<w:pBdr>` sitting between `widowControl` and `<w:spacing>`
inside `<w:pPr>`, (2) reuse of the Phase 4b `BorderSide` primitive
for paragraph borders, and (3) the full attribute set
`val / color / sz / space` including `space` which most other border
fixtures leave `null`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "auto", space: 1 } }, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
