# Fixture: footer-default

**What this demonstrates:** a document with a single default-type
footer and no header.

- `word/document.xml` — `<w:footerReference w:type="default"
  r:id="rId1"/>` inside `<w:sectPr>`. rId is `rId1` (not `rId2`)
  because there's no header preceding it in our allocator.
- `word/footer1.xml` — `<w:ftr xmlns...>` with the narrower
  footer-namespace subset (smaller than the header's — upstream
  quirk we mirror) and one paragraph child.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ footers: { default: new Footer({ children: [...] }) }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`
- `word/footer1.xml`

**Modifications from raw upstream extraction:**
- `<w:footerReference>` `r:id` rewritten from upstream's `rId8` to
  our library's `rId1` (no header, so footer takes the first rId
  slot). Publisher handles this normalization.
