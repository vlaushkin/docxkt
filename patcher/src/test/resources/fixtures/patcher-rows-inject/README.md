# Fixture: patcher-rows-inject

**What this demonstrates:** `Patch.Rows` — a `{{rows}}`
marker inside a table cell triggers replacement of the
ENCLOSING `<w:tr>` with snippet rows.

- Input: 1×1 table with `Header` row + `{{rows}}` row.
- Patch: `{"rows" → Patch.Rows(tableRows {
    row { cell { paragraph { text("Alice") } } }
    row { cell { paragraph { text("Bob") } } }
  })}`.
- Output: 1×1 table with `Header` + `Alice` + `Bob` rows.

The marker row is spliced out; snippet rows replace it.

**Compared XML parts:**
- `word/document.xml`
