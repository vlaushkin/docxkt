# Fixture: header-and-footer

**What this demonstrates:** both header and footer present. Locks the
rId allocation order (header = `rId1`, footer = `rId2`) and the
emission order inside `<w:sectPr>` (headerReference before
footerReference, both before `<w:pgSz>`).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ headers: { default: Header }, footers: { default: Footer }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`
- `word/footer1.xml`

**Modifications from raw upstream extraction:**
- `<w:headerReference>` / `<w:footerReference>` `r:id` values
  rewritten to the `rId1` / `rId2` allocator pair.
