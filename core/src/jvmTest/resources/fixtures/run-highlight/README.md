# Fixture: run-highlight

**What this demonstrates:** `<w:highlight w:val="yellow"/>
<w:highlightCs w:val="yellow"/>` — a value from the closed
`HighlightColor` enum, plus the auto-mirrored complex-script pair.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, highlight: HighlightColor.YELLOW })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
