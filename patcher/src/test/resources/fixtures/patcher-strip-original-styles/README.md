# Fixture: patcher-strip-original-styles

**What this demonstrates:** `keepOriginalStyles = false`.
The marker sits inside a bold-formatted run; the replacement
text loses the bold formatting.

- Input: bold run `Hello {{name}}!`.
- Patch: `{"name" → Patch.Text("Alice")}`, with
  `keepOriginalStyles = false`.
- Output: three runs:
  - bold `Hello `
  - bare `Alice` (no rPr)
  - bold `!`

The implementation splits the source run into the prefix
half (kept bold), inserts a fresh bare `<w:r>` for the
replacement, and inserts a clone of the source run for the
suffix half.

With `keepOriginalStyles = true` (the default) the wire would
have been a single bold run `Hello Alice!`.

**Compared XML parts:**
- `word/document.xml`
