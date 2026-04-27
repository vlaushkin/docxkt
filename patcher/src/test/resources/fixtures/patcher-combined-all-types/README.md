# Fixture: patcher-combined-all-types

**What this demonstrates:** all four concrete patch types in
one document, applied in a single `PatchDocument.patch` call.

Markers in input:
- `Hello {{name}}!` — Patch.Text
- `{{intro}}` (alone in a paragraph) — Patch.Paragraphs
- `Logo: {{logo}}.` — Patch.Image
- table cell containing `{{records}}` — Patch.Rows

Patches:
- `name → Patch.Text("Alice")`
- `intro → Patch.Paragraphs(2 paragraphs)`
- `logo → Patch.Image(67-byte 1×1 PNG, ...)`
- `records → Patch.Rows(2 rows)`

Output combines all four mutations:
- `Hello Alice!`
- `Welcome.` / `Glad to have you.`
- `Logo: ` + `<w:drawing rId1>` + `.`
- table with `Header` + `Row1` + `Row2`

**Compared XML parts:**
- `word/document.xml`
- `[Content_Types].xml`
- `word/_rels/document.xml.rels`

**Compared binary parts:**
- `word/media/image1.png`

This is the integration test for the entire patcher.
