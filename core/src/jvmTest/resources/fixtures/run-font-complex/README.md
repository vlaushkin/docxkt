# Fixture: run-font-complex

**What this demonstrates:** `<w:rFonts w:ascii="Calibri" w:cs="Arial"
w:eastAsia="MS Mincho" w:hAnsi="Cambria"/>` — each of the four
character ranges gets its own font. Locks the attribute order
(`ascii, cs, eastAsia, hAnsi`) regardless of input argument order.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, font: { ascii, hAnsi, cs, eastAsia } })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
