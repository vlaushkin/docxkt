# Fixture: demo-58-section-types

Phase 38 port of `/opt/docx-ref/demo/58-section-types.ts`.

Five sections, each ending with a different `<w:type>` value:
default (no type), `continuous`, `oddPage`, `evenPage`, `nextPage`.
The body shows the section-end paragraphs with their respective
section properties. Closes the Phase 37b "SectionType not modelled"
gap.

**Compared XML parts:** `word/document.xml`
