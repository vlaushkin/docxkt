# Fixture: patcher-non-recursive

**What this demonstrates:** `recursive = false` — each key
is replaced at most once per `patch()` call.

- Input: two paragraphs each containing `{{key}}`.
- Patch: `{"key" → Patch.Text("VALUE")}`, with
  `recursive = false`.
- Output: ONLY the first occurrence is replaced. The second
  `{{key}}` remains literal.

With `recursive = true` (the default), both would replace.
With the option flipped, the implementation drops the key
from the active map after its first match.

**Compared XML parts:**
- `word/document.xml`
