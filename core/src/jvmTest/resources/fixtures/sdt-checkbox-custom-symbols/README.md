# Fixture: sdt-checkbox-custom-symbols

**What this demonstrates:** third Phase 24 fixture. Checkbox
with custom Wingdings symbols (F0FE/F0A8) overriding the
default MS Gothic 2612/2610 pair. Asserts both
`<w14:checkedState>` and `<w14:uncheckedState>` carry the
overridden `w14:val` and `w14:font`, and that the rendered
`<w:sym>` uses the active state's symbol/font.

**Compared XML parts:** `word/document.xml`
