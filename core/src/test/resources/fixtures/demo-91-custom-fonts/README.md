# Fixture: demo-91-custom-fonts

Phase 39 port of `/opt/docx-ref/demo/91-custom-fonts.ts`.

A single paragraph that sets `runDefaults { font(name = "Pacifico") }`
plus three text runs each explicitly carrying `font(name = "Pacifico")`.
The third run uses a leading tab (Phase 38 RunScope.tab()).

The actual font binary embedding (`<w:font w:name="Pacifico">…</w:font>`
in fontTable.xml + `word/fonts/font1.odttf`) is not modelled; we
compare ONLY `word/document.xml`.

**Compared XML parts:** `word/document.xml`
