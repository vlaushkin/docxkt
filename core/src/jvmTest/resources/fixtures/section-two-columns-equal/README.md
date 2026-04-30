# Fixture: section-two-columns-equal

**What this demonstrates:** `<w:cols>` with `count=2` and
`equalWidth=true`, 720-twip gap, no separator.

Wire shape (locked):

```xml
<w:cols w:space="720" w:num="2" w:equalWidth="true"/>
```

Notes:
- Attribute order `space → num → sep → equalWidth` (upstream
  `BuilderElement` push order — NOT alphabetical).
- Boolean values emit as literal `"true"`/`"false"`, NOT
  the OOXML-canonical `"1"`/`"0"`. Upstream's BuilderElement
  passes JS booleans through to its string formatter.
- Self-closed (no `<w:col>` children) because
  `equalWidth=true`.
- Lives between `<w:pgNumType/>` and `<w:docGrid/>` in
  `<w:sectPr>`.

**Compared XML parts:**
- `word/document.xml`
