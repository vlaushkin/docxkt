# Fixture: paragraph-spacing

**What this demonstrates:** `<w:spacing w:after="240" w:before="120"
w:line="360" w:lineRule="auto"/>`. Locks attribute order
(`after, before, line, lineRule, ...`) per upstream's
`BuilderElement`, and the `LineRule.AUTO` enum → wire mapping.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ spacing: { before: 120, after: 240, line: 360, lineRule: LineRuleType.AUTO } })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
