# Fixture: patcher-rows-from-data

**What this demonstrates:** data-driven rows — the test
constructs the snippet rows from a Kotlin `List<Pair<String,
String>>` via a `for` loop. Same wire result as
`patcher-rows-inject` but exercises the path where rows are
built dynamically rather than literally.

- Input: 2-column table with header (`Name`, `Score`) +
  marker row.
- Patch: `Patch.Rows(tableRows { for ((name, score) in
  data) row { cell { text(name) }; cell { text(score) } } })`
  with 3 data records (Alice/95, Bob/82, Carol/78).
- Output: header row + 3 data rows.

**Compared XML parts:**
- `word/document.xml`
