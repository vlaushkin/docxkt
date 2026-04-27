# Fixture: demo-100-table-look

Phase 38 port of `/opt/docx-ref/demo/100-table-look.ts`.

Two 4×3 tables both styled with `MyCustomTableStyle` and width
100%. The first uses default tableLook; the second sets all six
flags (`firstRow`, `lastRow`, `firstColumn`, `lastColumn`,
`noHBand=false`, `noVBand=false`). Closes the Phase 37b
"tblLook table look" gap.

**Compared XML parts:** `word/document.xml`
