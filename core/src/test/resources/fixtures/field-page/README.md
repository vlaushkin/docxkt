# Fixture: field-page

**What this demonstrates:** second Phase 15 fixture. A
complex-form `PAGE` field inside a paragraph. Asserts the
single-run `<w:r>` wire containing begin/instrText/separate/end
(no cached value).

- `word/document.xml` — one paragraph, two runs: plain-text
  `"Page "` then the field run
  (`<w:r><w:fldChar begin/><w:instrText>PAGE</w:instrText>
  <w:fldChar separate/><w:fldChar end/></w:r>`).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
