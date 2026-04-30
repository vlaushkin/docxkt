# Fixture: demo-8-header-footer

Phase 40 port of `/opt/docx-ref/demo/8-header-footer.ts`.

A document with one header (default) and one footer (default).
The footer in upstream contains a numbered list, but only
`word/document.xml` is compared — the body just references
header/footer via rId7/rId8 (matching upstream after the Phase
40b allocator refactor).

Stand-in header/footer in the DSL just to register the references.
The footer's numbering content lives in `word/footer1.xml` / `word/numbering.xml`
(not compared).

**Compared XML parts:** `word/document.xml`
