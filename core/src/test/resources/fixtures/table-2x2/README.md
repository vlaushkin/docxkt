# Fixture: table-2x2

**What this demonstrates:** two rows of two cells each, no explicit
properties. Exercises row/cell iteration and the implicit
`<w:tblGrid>` sum: the grid gets one `<w:gridCol w:w="100"/>` per
logical column.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ rows: [new TableRow(...), new TableRow(...)] })` — each
  row with two `TableCell`s.

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
