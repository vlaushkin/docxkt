# Fixture: comment-spanning-runs

**What this demonstrates:** second Phase 19 fixture. Comment
brackets wrap two runs with distinct rPr (one bold, one plain)
— asserts commentRangeStart/End don't break mid-run and that
each bracketed run keeps its own rPr.

**Compared XML parts:**
- `word/document.xml`
- `word/comments.xml`
