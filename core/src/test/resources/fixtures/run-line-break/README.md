# Fixture: run-line-break

**What this demonstrates:** a soft `<w:br/>` between two text runs —
no `w:type` attribute, upstream's "plain break" default. The break
lives in its own `<w:r>` (standalone break-only run), so the wire
shape is `<w:r>line1</w:r><w:r><w:br/></w:r><w:r>line2</w:r>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun("line1")`
- `new TextRun({ break: 1 })` — break-only upstream run.
- `new TextRun("line2")`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
