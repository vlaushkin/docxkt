# Fixture: paragraph-shading

**What this demonstrates:** `<w:shd w:fill="EEEEEE" w:color="auto"
w:val="clear"/>` at paragraph level. Identical wire shape to
table-cell shading (Phase 4b) and run-level shading (Phase 2b) —
same helper covers all three.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ shading: { type: ShadingType.CLEAR, color: "auto", fill: "EEEEEE" }, children })`

**Compared XML parts:**
- `word/document.xml`
