# Fixture: demo-57-add-parent-numbered-lists

Phase 47 port. Two-level numbering with parent number visible
in sub-number text (`%1.%2`). Level 1 has run-style overrides
(bold, size 18, font Times New Roman) — currently we don't
emit run defaults inside `<w:lvl>` so we capture only what we
support and document any divergences.

**Compared XML parts:** auto-discovered.
