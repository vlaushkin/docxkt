# Fixture: bookmark-span

**What this demonstrates:** second Phase 13 fixture. A bookmark
spanning two paragraphs — `<w:bookmarkStart>` in the first
paragraph, `<w:bookmarkEnd>` in the second, linked by the same
`w:id`. The run content sits between them, split across the two
paragraphs.

- `word/document.xml` — two paragraphs:
  - first: `<w:bookmarkStart w:name="section" w:id="1"/><w:r>
    <w:t xml:space="preserve">Start of section</w:t></w:r>`.
  - second: `<w:r><w:t xml:space="preserve">End of section</w:t>
    </w:r><w:bookmarkEnd w:id="1"/>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- Upstream: `new BookmarkStart("section", 1)` + `new BookmarkEnd(1)`
  placed as paragraph children directly (bypassing the
  auto-pairing `Bookmark` wrapper, since the wrapper keeps
  start/end in one paragraph).
- Our DSL: `paragraph { bookmarkStart("section"); text(...) }` +
  `paragraph { text(...); bookmarkEnd("section") }`.

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None. Our per-document bookmark-id counter matches upstream's
  `bookmarkUniqueNumericIdGen()` — both produce `w:id="1"` for
  the single bookmark.
