# Fixture: demo-81-continuous-header

Phase 41 port of `/opt/docx-ref/demo/81-continuous-header.ts`.

Two-section document. Section 1 has `titlePage: true` plus
default + first headers and default + first footers (rId7-10).
Section 2 has `type: SectionType.CONTINUOUS` (no headers/footers
of its own, just continues section 1's). Body paragraphs are
mostly long lorem text — loaded from `paragraphs.json` to keep
the test source readable.

**Compared XML parts:** `word/document.xml`
