# Fixture: hello-world

**What this demonstrates:** a minimal wordprocessingML document with a single
paragraph containing a single run of plain text "Hello, world!".

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs` in this folder. Run inside the sandbox:
`node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ properties: {}, children: [...] }] })`
- `new Paragraph({ children: [new TextRun("Hello, world!")] })`
- `Packer.toBuffer(doc)`

**Compared XML parts:**

- `word/document.xml`
- `[Content_Types].xml`
- `_rels/.rels`

**Not compared:** dolanmiu/docx unconditionally emits ~17 ZIP entries even
for a one-paragraph document. For Phase 1 we compare only the three listed
above. Upstream also emits `word/styles.xml`, `word/numbering.xml`,
`word/settings.xml`, `word/fontTable.xml`, `word/footnotes.xml`,
`word/endnotes.xml`, `word/comments.xml`, `docProps/core.xml`,
`docProps/app.xml`, `docProps/custom.xml`, plus `word/_rels/*.rels` —
none of which Phase 1 owns.

**Modifications from raw upstream extraction:**

1. `word/document.xml` — the upstream `<w:body>` contains a final
   `<w:sectPr>` with page size, margins, page-number type, and doc grid.
   Section formatting is Phase 7; removed here so the fixture matches
   what Phase 1 emits. No other modifications.
2. `[Content_Types].xml` — upstream lists Default entries for image
   extensions (png, jpeg, jpg, bmp, gif, svg) and the obfuscated-font MIME,
   plus Override entries for every auxiliary part. Phase 1 emits only the
   parts it owns, so this fixture lists only those two Defaults (rels, xml)
   and one Override (word/document.xml). Image defaults return in Phase 9.
3. `_rels/.rels` — upstream has four relationships: officeDocument, core
   properties, extended properties, custom properties. Phase 1 does not
   own docProps, so this fixture lists only the officeDocument relationship.
