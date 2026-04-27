# Fixture: patcher-token-simple

**What this demonstrates:** the trivial token replacement
case — a single `{{name}}` placeholder fully contained in
one run, replaced by `Patch.Text("Alice")`.

- Input: `<w:p><w:r><w:t xml:space="preserve">Hello {{name}}!</w:t></w:r></w:p>`
- Patch: `{"name" → Patch.Text("Alice")}`
- Output: `<w:p><w:r><w:t xml:space="preserve">Hello Alice!</w:t></w:r></w:p>`

The `<w:t>` element is mutated in place; surrounding `<w:r>`
structure and any `<w:rPr>` are preserved.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Compared XML parts:**
- `word/document.xml`
