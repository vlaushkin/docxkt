# Fixture: header-default

**What this demonstrates:** a document with a single default-type
header and no footer.

- `word/document.xml` — section carries a
  `<w:headerReference w:type="default" r:id="rId1"/>` inside
  `<w:sectPr>`, before `<w:pgSz>`.
- `word/header1.xml` — `<w:hdr xmlns...>` with the narrower
  header-namespace subset and one paragraph child.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ headers: { default: new Header({ children: [...] }) }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`

**Modifications from raw upstream extraction:**
- `<w:headerReference r:id="...">` has its `r:id` rewritten from
  upstream's `rId7` (a side effect of upstream owning styles,
  numbering, and other parts at lower rIds) to our library's `rId1`.
  The publisher (`/opt/fixtures/publish.mjs`) handles this
  normalization.
