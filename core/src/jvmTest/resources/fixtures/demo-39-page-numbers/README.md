# Fixture: demo-39-page-numbers

Phase 40 port of `/opt/docx-ref/demo/39-page-numbers.ts`.

Single section with default header + footer (rId7/rId8) and
`<w:pgNumType w:start="1" w:fmt="decimal"/>`. Body has 5
paragraphs each ending with a page break. Closes Phase 37b
"pageNumbers.start + pageNumbers.separator" gap (the demo's
section uses pageNumbers `start: 1, formatType: DECIMAL`).

**Compared XML parts:** `word/document.xml`
