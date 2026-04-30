# Fixture: demo-31-tables

Phase 39 port of `/opt/docx-ref/demo/31-tables.ts`.

A 4×2 table mixing `verticalAlign(CENTER)` cells with two
`textDirection` cells (btLr + tbRl) and a Heading1-styled
paragraph in one cell. Re-port enabled by Phase 38's
`TableCellScope.textDirection(...)`.

**Compared XML parts:** `word/document.xml`
