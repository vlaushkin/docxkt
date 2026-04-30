# Fixture: demo-25-table-xml-styles

Phase 38 port of `/opt/docx-ref/demo/25-table-xml-styles.ts`.

A 2×2 table that references the externally-defined style
`MyCustomTableStyle` via `<w:tblStyle w:val="..."/>`. Width is
DXA=9070. Closes the Phase 37b "tblStyle on TableScope" gap by
exercising the new `TableScope.styleReference`.

**Compared XML parts:** `word/document.xml`
