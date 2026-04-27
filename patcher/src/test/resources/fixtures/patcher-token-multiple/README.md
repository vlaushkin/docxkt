# Fixture: patcher-token-multiple

**What this demonstrates:** multiple `{{key}}` markers in
one document, each in its own paragraph.

- Input paragraphs:
  - `Dear {{name}},`
  - `Welcome to {{company}}.`
  - `Your role: {{role}}.`
- Patches:
  - `name → Alice`
  - `company → ACME`
  - `role → Engineer`
- Output paragraphs (text only):
  - `Dear Alice,`
  - `Welcome to ACME.`
  - `Your role: Engineer.`

The replacer's outer loop walks every paragraph and applies
patches independently, so order is irrelevant.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Compared XML parts:**
- `word/document.xml`
