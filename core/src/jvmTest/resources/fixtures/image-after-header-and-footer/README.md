# Fixture: image-after-header-and-footer

**What this demonstrates:** a document with all three kinds of
document-scoped relationships Phase 6 + 7 produce. Locks the
`RelationshipAllocator` sequence:

- header default → `rId1`
- footer default → `rId2`
- inline image   → `rId3`

Compares four parts (three XML + one binary):

- `word/document.xml` — sectPr has `<w:headerReference r:id="rId1"/>`
  then `<w:footerReference r:id="rId2"/>` then `<w:pgSz>`; the body
  paragraph's `<a:blip>` has `r:embed="rId3"`.
- `word/header1.xml` — header content.
- `word/footer1.xml` — footer content.
- `word/media/image1.png` — image payload.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ headers, footers, children: [Paragraph with ImageRun] }] })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`
- `word/footer1.xml`

**Compared binary parts:**
- `word/media/image1.png`

**Modifications from raw upstream extraction:**
- All upstream rIds (`rId7` header, `rId8` footer, `rId9` image or
  whatever upstream happens to allocate) are rewritten to our
  allocator's `rId1` / `rId2` / `rId3` sequence.
- Image filename rewritten from `<sha1>.png` to `image1.png`.
