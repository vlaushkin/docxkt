# Fixture: run-hyphens

**What this demonstrates:** the two hyphen empty-elements as
run children:

- `<w:softHyphen/>` — optional hyphen, renders only when the
  word wraps. Used here in `co<softHyphen>operate`.
- `<w:noBreakHyphen/>` — visible hyphen that prevents
  wrapping. Used in `up<noBreakHyphen>to`.

Each hyphen lives in its own bare `<w:r>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish with `publish.mjs
run-hyphens`.

**Compared XML parts:**
- `word/document.xml`
