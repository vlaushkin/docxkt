# Fixture: demo-44-multiple-columns

Phase 39 port of `/opt/docx-ref/demo/44-multiple-columns.ts`.

Three sections, each with a different `<w:cols>` configuration:
- 2 columns, space=708
- 3 columns, space=708
- 2 columns, space=708, separator=true

Each section has a header paragraph + a long lorem-ipsum body
paragraph. The 2-column and 3-column lorem texts differ slightly
in length; both 2-column sections share the same text. Stored as
`lorem-2col.txt` and `lorem-3col.txt` — too long to inline.

**Compared XML parts:** `word/document.xml`
