# Fixture: patcher-paragraph-mid-split

**What this demonstrates:** mid-paragraph split — the marker
`{{block}}` is surrounded by other text. The source paragraph
splits into a "before" half (`"before "`) and an "after" half
(`" after"`), with the patch's snippet paragraph (`"INSERTED"`)
inserted between them.

- Input: `<w:p>before {{block}} after</w:p>` (single paragraph).
- Patch: `{"block" → Patch.Paragraphs(paragraphs { paragraph
  { text("INSERTED") } })}`.
- Output: 3 paragraphs (`before `, `INSERTED`, ` after`).

The before/after halves preserve the original paragraph's
`<w:r>` structure, with text trimmed to the relevant slice.

**Compared XML parts:**
- `word/document.xml`
