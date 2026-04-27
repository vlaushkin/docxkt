# Fixture: footnote-with-formatting

**What this demonstrates:** fourth Phase 17 fixture. Footnote
content has a run with `bold` rPr plus a plain trailing run.
Asserts run-level formatting round-trips inside the footnote
body — no loss of rPr from the auto-prepend step.

**Compared XML parts:**
- `word/document.xml`
- `word/footnotes.xml`
