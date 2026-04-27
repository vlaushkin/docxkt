# Fixture: table-grid-span

**What this demonstrates:** a row where one cell spans two logical
grid columns via `<w:gridSpan w:val="2"/>`. Two-row layout so the
grid (2 columns, 1500 twips each) is exercised by the unmerged second
row too.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TableCell({ columnSpan: 2, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
