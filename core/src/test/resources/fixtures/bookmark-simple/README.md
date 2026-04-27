# Fixture: bookmark-simple

**What this demonstrates:** first Phase 13 fixture. A single
paragraph containing a bookmark that wraps one run: start / run /
end, all inline inside the same `<w:p>`.

- `word/document.xml` — one paragraph with three children in
  order: `<w:bookmarkStart w:name="intro" w:id="1"/>`,
  `<w:r><w:t xml:space="preserve">Introduction</w:t></w:r>`,
  `<w:bookmarkEnd w:id="1"/>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ children: [new Bookmark({ id, children: [TextRun] })] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None. Our bookmark-id counter starts at 1 and increments
  sequentially; upstream's `bookmarkUniqueNumericIdGen()` does
  the same per-document. The single bookmark gets `w:id="1"` on
  both sides, so no publisher rewrite is needed.

**Notes:**
- `<w:bookmarkStart>` attribute order is `w:name` then `w:id` —
  matches upstream's `BookmarkStart` constructor-options
  population order (not the xmlKeys declaration order). Don't
  swap.
