# Fixture: demo-14-page-numbers

Phase 40 port of `/opt/docx-ref/demo/14-page-numbers.ts`.

A single-section document with default + first headers and
default + first footers (rId7-10). Body has two paragraphs
(first with page break). Setting a `FIRST` header
auto-enables `<w:titlePg/>` in the section.

The header/footer contents have `PageNumber.CURRENT` fields
inside TextRun children — but only `word/document.xml` is
compared, body has no PageNumber fields.

**Compared XML parts:** `word/document.xml`
