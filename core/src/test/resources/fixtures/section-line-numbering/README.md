# Fixture: section-line-numbering

**What this demonstrates:** `<w:lnNumType>` element inside
`<w:sectPr>` — line numbering for a section.

Wire shape (locked):

```xml
<w:lnNumType w:countBy="1" w:start="1" w:restart="continuous" w:distance="720"/>
```

Notes:
- Attribute order: `countBy → start → restart → distance`
  (matches upstream `BuilderElement` push order in
  `line-number.ts`).
- `restart` value is the `ST_LineNumberRestart` token
  (`continuous` / `newPage` / `newSection`).
- Element is positioned BETWEEN `<w:pgMar>` and
  `<w:pgNumType/>` per upstream's section-properties
  constructor push order (`pgBorders → lnNumType →
  pgNumType`).

**Compared XML parts:**
- `word/document.xml`
