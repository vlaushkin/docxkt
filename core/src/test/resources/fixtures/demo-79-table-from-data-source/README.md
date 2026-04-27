# Fixture: demo-79-table-from-data-source

Phase 39 port of `/opt/docx-ref/demo/79-table-from-data-source.ts`.

A 14-row stock-price table built programmatically. The header row
has Heading2-styled cells with `verticalAlign(CENTER)` +
`textDirection(LRT or TBR)`. Each data row mirrors the same cell
layout. Date strings are JS `Date.toString()` output in UTC — the
exact "Tue Aug 28 2007 00:00:00 GMT+0000 (Coordinated Universal
Time)" form. Re-port enabled by Phase 38's textDirection.

**Compared XML parts:** `word/document.xml`
