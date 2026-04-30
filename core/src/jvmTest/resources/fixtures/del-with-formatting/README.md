# Fixture: del-with-formatting

**What this demonstrates:** fourth Phase 20 fixture. Deleted
run carries italics rPr inside its inner `<w:r>` — asserts
the `DeletedRun` emitter preserves the run's rPr alongside
the `<w:t>` → `<w:delText>` rewrite.

**Compared XML parts:**
- `word/document.xml`
