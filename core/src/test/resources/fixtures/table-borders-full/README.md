# Fixture: table-borders-full

**What this demonstrates:** all six sides of `<w:tblBorders>` set to
non-default styles. Outer sides (top, left, bottom, right) use
DOUBLE / size 12 / #3366FF; inside sides (insideH, insideV) use
DASHED / size 8 / #CCCCCC.

Locks (1) `BorderStyle` enum → wire mapping for two non-SINGLE
styles, (2) `<w:top, w:left, w:bottom, w:right, w:insideH, w:insideV>`
emission order, (3) the per-side attribute order
`w:val, w:color, w:sz` (space omitted when null).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ borders: { top, left, bottom, right, insideHorizontal, insideVertical }, rows })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
