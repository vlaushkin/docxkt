# Fixture: patcher-paragraph-replace-line

**What this demonstrates:** whole-paragraph replacement —
the middle paragraph is `{{block}}` (marker is the entire
visible text); it gets replaced by two new paragraphs from
a `Patch.Paragraphs(snippets)` patch.

- Input: 3 paragraphs (`First`, `{{block}}`, `Last`).
- Patch: `{"block" → Patch.Paragraphs(paragraphs { paragraph
  { text("Inserted A") }; paragraph { text("Inserted B") } })}`.
- Output: 4 paragraphs (`First`, `Inserted A`, `Inserted B`,
  `Last`).

**Compared XML parts:**
- `word/document.xml`
