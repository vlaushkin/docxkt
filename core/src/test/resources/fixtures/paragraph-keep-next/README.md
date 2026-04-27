# Fixture: paragraph-keep-next

**What this demonstrates:** `<w:keepNext/>` inside `<w:pPr>` — the
OnOff true (attribute-free) form surfacing from `ParagraphProperties`
via the same `onOff` helper that RunProperties uses. Cheapest possible
regression against "pPr OnOffs silently emit `w:val="true"`".

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ keepNext: true, children: [...] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
