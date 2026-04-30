# Fixture: hyperlink-multi-run

**What this demonstrates:** Phase 12 stretch fixture. A single
hyperlink wrapping two runs with distinct `<w:rPr>` — first run
bold, second plain. Asserts the `<w:hyperlink>` container
preserves each child `<w:r>`'s own property block (no merging,
no coalescing).

- `word/document.xml` — one paragraph, one hyperlink, two
  `<w:r>` children inside: `<w:r><w:rPr><w:b/><w:bCs/></w:rPr>
  <w:t xml:space="preserve">bold </w:t></w:r><w:r>
  <w:t xml:space="preserve">plain</w:t></w:r>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ children: [ExternalHyperlink] })`
- `new ExternalHyperlink({ link, children: [TextRun({bold}),
  TextRun()] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- Hyperlink `r:id` rewritten to our allocator's `rId1`.
