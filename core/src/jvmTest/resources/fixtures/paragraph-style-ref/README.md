# Fixture: paragraph-style-ref

**What this demonstrates:** `<w:pStyle w:val="MyStyle"/>` as the very
first child of `<w:pPr>`. Mirrors Phase 2b's `run-style-ref` but for
paragraph style references.

Emission-only: the fixture ships with a style id that doesn't need
to resolve against anything (there's no styles part). Phase 6
(Styles) will ship the resolver; Phase 3b just ships the reference.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ style: "MyStyle", children: [...] })`

**Compared XML parts:**
- `word/document.xml`
