# Fixture: cell-borders

**What this demonstrates:** `<w:tcBorders>` with two sides set
(top + bottom), the other four omitted. Unlike `<w:tblBorders>`,
cell borders do **not** default-fill unset sides — only what the
caller passed is emitted. Our `TableCellBorders` mirrors this.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TableCell({ borders: { top: ..., bottom: ... }, children })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
