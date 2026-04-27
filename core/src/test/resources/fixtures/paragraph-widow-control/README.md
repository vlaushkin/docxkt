# Fixture: paragraph-widow-control

**What this demonstrates:** `<w:widowControl w:val="false"/>` — the
OnOff `w:val="false"` form in the paragraph context. Word's default
is `true`, so the interesting branch is an explicit `false`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ widowControl: false, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
