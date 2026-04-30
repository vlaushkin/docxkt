# Fixture: bookmark-with-hyperlink

**What this demonstrates:** Phase 13 stretch fixture. Combines
Phase 12 (hyperlinks) with Phase 13 (bookmarks). A bookmark
`target` is defined in the first paragraph; the second
paragraph contains an internal hyperlink (`<w:hyperlink
w:anchor="target">`) pointing at it. No relationship entry —
internal hyperlinks resolve by name inside Word.

- `word/document.xml`:
  - first paragraph: bookmark wrapping a run labeled
    "Target heading".
  - second paragraph: three runs — `See `, hyperlink wrapping
    `the target` with `w:anchor="target"` (no `r:id`), and
    ` above.`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Bookmark({ id, children })` — auto-emits paired
  bookmarkStart / bookmarkEnd.
- `new InternalHyperlink({ anchor, children })` — emits
  `<w:hyperlink w:anchor="…">` without an rId.

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None. No rIds involved (the internal hyperlink doesn't
  allocate one); bookmark id counter matches between our
  allocator and upstream's per-document generator.

**Invariant locked:** internal hyperlinks emit `w:anchor` in
place of `r:id` alongside the unconditional `w:history="1"`.
