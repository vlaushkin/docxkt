# Fixture: demo-49-table-borders

Phase 39 port of `/opt/docx-ref/demo/49-table-borders.ts`.

Two tables exercising:
- Cell-level `<w:tcBorders>` with dashSmallGap on all four sides.
- Whole-table `bordersAllNone()` (the upstream `TableBorders.NONE`
  convenience), with cells using vertical alignment + textDirection
  (Phase 38 textDirection unblocker).

**Compared XML parts:** `word/document.xml`
