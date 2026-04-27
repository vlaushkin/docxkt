# Fixture: cell-margins-table

**What this demonstrates:** `<w:tblCellMar>` inside `<w:tblPr>` with
four sides set (top/left/bottom/right in twips). Locks child emission
order `top → left → bottom → right` and the `<w:tblWidth>`-shape
attribute order (`w:type, w:w`) on each side.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ margins: { top: 100, left: 120, bottom: 100, right: 120 }, rows })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
