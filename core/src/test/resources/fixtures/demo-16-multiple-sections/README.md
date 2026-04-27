# Fixture: demo-16-multiple-sections

Phase 41 port of `/opt/docx-ref/demo/16-multiple-sections.ts`.

Six-section document, each with its own header (some also footer)
plus pageNumbers configuration. Closes the Phase 37a "multi-section
per-section header/footer" architectural gap via the Phase 41a
refactor — `SectionBreakScope.header(...)` / `footer(...)` register
section-specific H/F that get their own rIds (rId7-13 in source
order).

The demo's headers contain PageNumber.CURRENT fields inside TextRun
children (separate gap, not in body). We compare ONLY
`word/document.xml` so header/footer content differences don't
affect the diff.

**Compared XML parts:** `word/document.xml`
