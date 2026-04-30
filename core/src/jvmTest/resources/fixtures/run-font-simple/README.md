# Fixture: run-font-simple

**What this demonstrates:** `<w:rFonts w:ascii="Arial" w:cs="Arial"
w:eastAsia="Arial" w:hAnsi="Arial"/>` — passing a single name to
upstream's `createRunFonts("Arial")` fan-outs it to all four character
ranges. Locks the attribute order `ascii, cs, eastAsia, hAnsi`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, font: "Arial" })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
