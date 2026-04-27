# Fixture: run-tab-character

**What this demonstrates:** the `<w:tab/>` empty element as a
run child. Three runs in one paragraph:

- `<w:r><w:t xml:space="preserve">before</w:t></w:r>`
- `<w:r><w:tab/></w:r>`
- `<w:r><w:t xml:space="preserve">after</w:t></w:r>`

Distinct from `<w:tabs>` (paragraph-level tab-stop
definitions) — this is the actual control character.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish with `publish.mjs
run-tab-character`.

**Compared XML parts:**
- `word/document.xml`
