# Fixture: patcher-token-spanning-runs

**What this demonstrates:** marker spans run boundaries.
The `{{` opens in a bold run and `name}}` closes in a
default-formatted run.

The replacement text inherits the FIRST contributing run's
formatting (the bold run, per `keepOriginalStyles: true`
upstream default). The trailing `!` keeps its original
default-formatted run; only the marker characters in the
second run are removed.

- Input runs: `bold("Hello {{")`, `default("name}}!")`.
- Patch: `{"name" → Patch.Text("Alice")}`.
- Output runs: `bold("Hello Alice")`, `default("!")`.

The replacement text length differs from the marker length
(`Alice` vs `name`), proving the algorithm handles the
mismatch correctly.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Compared XML parts:**
- `word/document.xml`
