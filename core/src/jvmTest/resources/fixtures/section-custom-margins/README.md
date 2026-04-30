# Fixture: section-custom-margins

**What this demonstrates:** `<w:pgMar w:top="720" w:right="1000"
w:bottom="720" w:left="1000" w:header="360" w:footer="360"
w:gutter="0"/>` — non-default margin values. Locks the attribute
order (`top, right, bottom, left, header, footer, gutter`) of
`<w:pgMar>` against upstream.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ properties: { page: { margin: { top, right, bottom, left, header, footer, gutter } } }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
