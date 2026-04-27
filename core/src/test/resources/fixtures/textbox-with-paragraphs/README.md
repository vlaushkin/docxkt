# Fixture: textbox-with-paragraphs

**What this demonstrates:** second Phase 23 fixture. Three
paragraphs inside a textbox — one with bold rPr. Asserts
`<w:txbxContent>` body emits multiple `<w:p>` verbatim and
that paragraph-level rPr round-trips cleanly through the
textbox wrapper.

**Compared XML parts:** `word/document.xml`
