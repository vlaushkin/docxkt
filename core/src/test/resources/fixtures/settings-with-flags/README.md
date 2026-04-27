# Fixture: settings-with-flags

**What this demonstrates:** fourth Phase 14 fixture. The
`settings { }` DSL block with three OnOff flags enabled —
asserts the emission order (trackRevisions → evenAndOddHeaders
→ updateFields) and the truthy OnOff form
(`<w:X/>` for `true`, `<w:X w:val="false"/>` only for the
default-off `evenAndOddHeaders`).

- `word/settings.xml` — emits
  `<w:displayBackgroundShape/>`,
  `<w:trackRevisions/>`,
  `<w:evenAndOddHeaders/>`,
  `<w:updateFields/>`,
  then `<w:compat>…</w:compat>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`.

**Compared XML parts:**
- `word/settings.xml`

**Modifications from raw upstream extraction:**
- None.
