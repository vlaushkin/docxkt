# Fixture: patcher-paragraph-multiple

**What this demonstrates:** two `Patch.Paragraphs` markers in
two different paragraphs, each replaced by its own snippet list.

- Input: 5 paragraphs (`Top`, `{{first}}`, `Middle`,
  `{{second}}`, `Bottom`).
- Patches:
  - `first → 2 paragraphs (F1, F2)`
  - `second → 1 paragraph (S1)`
- Output: 6 paragraphs (`Top`, `F1`, `F2`, `Middle`, `S1`,
  `Bottom`).

The injector's outer loop re-walks after each application, so
both markers get processed in source order.

**Compared XML parts:**
- `word/document.xml`
