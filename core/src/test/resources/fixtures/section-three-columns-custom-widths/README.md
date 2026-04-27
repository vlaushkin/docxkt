# Fixture: section-three-columns-custom-widths

**What this demonstrates:** `<w:cols>` with `equalWidth=false`
and three `<w:col>` children of explicit widths and per-column
spaces.

Wire shape (locked):

```xml
<w:cols w:num="3" w:equalWidth="false">
  <w:col w:w="3000" w:space="360"/>
  <w:col w:w="4000" w:space="360"/>
  <w:col w:w="2500"/>
</w:cols>
```

Notes:
- Children emit only when `equalWidth != true`.
- `<w:col>` attribute order is `w → space`, with `space`
  omitted when null.
- The third column has no `space` attribute (last column,
  no following gap).

**Compared XML parts:**
- `word/document.xml`
