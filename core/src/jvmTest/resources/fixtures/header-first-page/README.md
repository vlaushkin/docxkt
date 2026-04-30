# Fixture: header-first-page

**What this demonstrates:** a section with both a default-type
header AND a first-page-only header. Section properties carry:

- `<w:headerReference w:type="default" r:id="rId1"/>` →
  `header1.xml`
- `<w:headerReference w:type="first" r:id="rId2"/>` →
  `header2.xml`
- `<w:titlePg/>` after `<w:pgNumType/>`

No even header, so settings.xml is unchanged from the default
phase-14 baseline.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Use `publish.mjs header-first-page
word/header1.xml word/header2.xml` to regenerate.

**API calls:**
- `new Document({ sections: [{
    properties: { titlePage: true },
    headers: { default: ..., first: ... },
    children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`
- `word/header2.xml`

**Modifications from raw upstream extraction:**
- Header reference `r:id` rewritten from upstream's `rId7` /
  `rId8` (a side effect of upstream owning styles, numbering,
  etc. at lower rIds) to our library's `rId1` / `rId2`.
