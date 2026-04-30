# Fixture: run-language-variants

**What this demonstrates:** `<w:lang>` with both the full triple and
the single-attribute (partial) form:

- Run 1: `<w:lang w:val="en-US" w:eastAsia="ja-JP" w:bidi="ar-SA"/>`
  — locks attribute order (`val, eastAsia, bidi` — not
  alphabetical).
- Run 2: `<w:lang w:val="en-US"/>` — verifies unset attributes are
  omitted from the wire, not emitted as empty strings.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, language: { value: "en-US", eastAsia: "ja-JP", bidirectional: "ar-SA" } })`
- `new TextRun({ text, language: { value: "en-US" } })`

**Compared XML parts:**
- `word/document.xml`
