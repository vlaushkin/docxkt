# Fixture: demo-27-declarative-styles-3

Phase 37b port of `/opt/docx-ref/demo/27-declarative-styles-3.ts`.

Two paragraphs, each referencing a paragraph-style id
(`myWonkyStyle`, `Heading2`) via `<w:pStyle w:val="..."/>`.
We compare ONLY `word/document.xml`; the styles.xml entries
upstream creates are external state.

**Compared XML parts:** `word/document.xml`
