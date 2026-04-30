# Fixture: page-borders-decorative

**What this demonstrates:** `<w:pgBorders>` inside `<w:sectPr>`.
All four sides set to a single 24-eighth-point red border with
`space=24`, plus the top-level attributes `display=allPages`,
`offsetFrom=page`, `zOrder=front`.

Locks two non-obvious orderings:

1. `<w:pgBorders>` sits between `<w:pgMar>` and `<w:pgNumType>`
   inside `<w:sectPr>`.
2. Side child order is `top → left → bottom → right` (XSD
   order — distinct from upstream's paragraph-border `top →
   bottom → left → right` quirk).
3. Page-borders top-level attribute order is `display →
   offsetFrom → zOrder` (matches upstream's
   `PageBordersAttributes.xmlKeys`, NOT alphabetical).
4. Each side's attribute order is `val → color → sz → space`
   (shared with all other border emitters).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ properties: { page: { borders: {
  pageBorders: { display, offsetFrom, zOrder },
  pageBorderTop, pageBorderLeft, pageBorderBottom, pageBorderRight,
  } } }, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
