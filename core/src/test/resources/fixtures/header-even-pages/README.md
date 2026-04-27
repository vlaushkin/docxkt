# Fixture: header-even-pages

**What this demonstrates:** a section with default + even
header types. Body sectPr carries:

- `<w:headerReference w:type="default" r:id="rId1"/>` →
  `header1.xml`
- `<w:headerReference w:type="even" r:id="rId2"/>` →
  `header2.xml`

`word/settings.xml` carries `<w:evenAndOddHeaders/>` — required
for Word/LibreOffice to honour the even-typed reference. No
`<w:titlePg/>` (no first header).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish with `publish.mjs
header-even-pages word/header1.xml word/header2.xml
word/settings.xml`.

**API calls:**
- `new Document({
    evenAndOddHeaderAndFooters: true,
    sections: [{ headers: { default: ..., even: ... }, ... }],
  })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`
- `word/header2.xml`
- `word/settings.xml` — for the `<w:evenAndOddHeaders/>`
  flag.

**Modifications from raw upstream extraction:**
- Header reference `r:id` rewritten upstream-rId7/rId8 →
  `rId1`/`rId2`.
