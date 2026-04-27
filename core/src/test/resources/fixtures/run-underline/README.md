# Fixture: run-underline

**What this demonstrates:** `<w:u w:val="double" w:color="FF0000"/>` —
underline with a non-default type and an explicit RGB color. Catches
regressions in the `w:u` attribute order (val, color) and in the
enum-to-wire mapping.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, underline: { type: UnderlineType.DOUBLE, color: "FF0000" } })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
