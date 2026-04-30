# Fixture: field-date

**What this demonstrates:** first Phase 15 fixture. A simple
`<w:fldSimple>` wrapping a DATE instruction with a format
switch — exercises XML attribute escaping when the instruction
string contains quotes.

- `word/document.xml` — one paragraph with a `<w:fldSimple
  w:instr="DATE \@ &quot;MMM d, yyyy&quot;"/>` followed by a
  plain-text run.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None. Our SimpleField instruction escaping produces the same
  `&quot;` entities upstream emits through its `BuilderElement`
  attribute serializer.
