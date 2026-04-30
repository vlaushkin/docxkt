# Fixture: demo-21-bookmarks

Phase 40 port of `/opt/docx-ref/demo/21-bookmarks.ts`.

A document with:
- Heading1-styled bookmark anchor "myAnchorId" wrapping "Lorem Ipsum".
- Paragraph with literal newline character.
- Long lorem-ipsum paragraph.
- Page break paragraph.
- Internal hyperlink with two styled runs pointing at the anchor.
- Paragraph with `PAGEREF myAnchorId` complex field.
- Footer (rId7 in sectPr).

Re-port enabled by Phase 40b's allocator refactor + new
`pageReference(name)` method (3-marker complex field, no
separate / no cached text).

**Compared XML parts:** `word/document.xml`
