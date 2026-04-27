# Fixture: paragraph-indent

**What this demonstrates:** `<w:ind w:left="720" w:right="360"
w:firstLine="240"/>` — indentation attributes in twips, with the
attribute order our RunFonts-style BuilderElement lock-in inherits
from `start, end, left, right, hanging, firstLine`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ indent: { left: 720, right: 360, firstLine: 240 }, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
