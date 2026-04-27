# Fixture: patcher-custom-delimiters

**What this demonstrates:** custom `placeholderDelimiters`.
The marker uses `<%name%>` instead of `{{name}}`.

- Input: `Hello <%name%>!`
- Patches: `{"name" → Patch.Text("Alice")}`, with
  `placeholderDelimiters = "<%" to "%>"`.
- Output: `Hello Alice!`

The default `{{name}}` regex would NOT match this input, so
the test proves the option is honoured.

**Compared XML parts:**
- `word/document.xml`
