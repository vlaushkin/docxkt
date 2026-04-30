# Fixture: demo-41-merge-table-cells-2

Phase 53 port. Two tables exercising column-span and row-span:

- Table 1 (6 cols, 6 rows): grid-spans of 2 in rows 0,1,2 and a
  span-5 cell in row 4.
- Table 2 (6 cols, 6 rows): row 0 col 1 has rowSpan=2 → row 1
  has 5 source cells (continuation auto-injected by upstream).

**Compared XML parts:** auto-discovered.
