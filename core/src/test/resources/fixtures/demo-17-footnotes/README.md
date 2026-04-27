# Fixture: demo-17-footnotes

Phase 37b port of `/opt/docx-ref/demo/17-footnotes.ts`.

Two-section document. Each section has paragraphs that pepper
text with footnote references (ids 1-3 in section 1, 4-6 in
section 2). The footnotes themselves live in `word/footnotes.xml`
and aren't compared. Body XML carries six
`<w:footnoteReference w:id="…"/>` runs.

**Compared XML parts:** `word/document.xml`
