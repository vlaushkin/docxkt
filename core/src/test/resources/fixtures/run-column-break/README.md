# Fixture: run-column-break

**What this demonstrates:** `<w:br w:type="column"/>` —
column-typed break inside a bare run. Three runs:

- `<w:r><w:t xml:space="preserve">col1</w:t></w:r>`
- `<w:r><w:br w:type="column"/></w:r>`
- `<w:r><w:t xml:space="preserve">col2</w:t></w:r>`

Meaningful only inside a `<w:cols>`-configured section; without
columns, Word/LibreOffice may render the column break as a page
break or ignore it.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish with `publish.mjs
run-column-break`.

**Compared XML parts:**
- `word/document.xml`
