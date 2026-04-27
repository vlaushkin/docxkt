# Fixture: table-1x1

**What this demonstrates:** the smallest possible working table —
`<w:tbl>` → `<w:tblPr>` → `<w:tblGrid>` → one `<w:tr>` → one `<w:tc>`
→ paragraph. Pins the hierarchy and locks the default-width behaviour:
upstream emits `<w:tblW w:type="auto" w:w="100"/>` even when no width
is requested; our DSL mirrors the same default.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ rows: [new TableRow({ children: [new TableCell({ children: [new Paragraph(...)] })] })] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
  emits a default border block unconditionally).
