# Fixture: table-width-dxa

**What this demonstrates:** table and cell widths in the DXA unit
(twips). Verifies attribute order (`w:type`, `w:w`) on both
`<w:tblW>` and `<w:tcW>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ width: { size: 5000, type: WidthType.DXA }, columnWidths: [2500, 2500], rows: [...] })`
- `new TableCell({ width: { size: 2500, type: WidthType.DXA }, ... })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
