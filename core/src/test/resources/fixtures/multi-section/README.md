# Fixture: multi-section

**What this demonstrates:** mid-body section break — a
two-section document where the FIRST section is portrait and
the SECOND section is landscape.

Wire shape (locked):

- Paragraph 1: visible body content for section 1.
- Paragraph 2: empty `<w:p>` whose `<w:pPr>` carries a
  `<w:sectPr>` describing the section that ENDS at this
  paragraph (section 1's portrait pgSz).
- Paragraph 3: visible body content for section 2.
- Body trailing `<w:sectPr>`: describes the LAST section
  (section 2's landscape pgSz).

The mid-body sectPr's child order is identical to the
trailing one (`pgSz → pgMar → pgNumType → docGrid`). The
container is the only difference: the section-break sectPr
sits inside `<w:pPr>`, the trailing one sits directly inside
`<w:body>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish via `publish.mjs
multi-section`.

**API calls:**
- `new Document({ sections: [
    { properties: { page: { size: { orientation: PORTRAIT } } }, children: [...] },
    { properties: { page: { size: { orientation: LANDSCAPE } } }, children: [...] },
  ] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
