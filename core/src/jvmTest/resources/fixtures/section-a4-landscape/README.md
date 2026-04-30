# Fixture: section-a4-landscape

**What this demonstrates:** `<w:pgSz w:w="16838" w:h="11906"
w:orient="landscape"/>` — orientation carried on the pgSz element
and dimensions swapped relative to portrait. Locks our
`PageSize.a4(PageOrientation.LANDSCAPE)` factory against upstream.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ properties: { page: { size: { orientation: PageOrientation.LANDSCAPE } } }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
