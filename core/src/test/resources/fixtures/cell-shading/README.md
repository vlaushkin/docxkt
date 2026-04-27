# Fixture: cell-shading

**What this demonstrates:** `<w:shd w:fill="EEEEEE" w:color="auto"
w:val="clear"/>` — the idiomatic "solid light-gray background" cell
shading. `val="clear"` means "no pattern"; `fill` carries the
background color. Locks the non-obvious attribute order
(`fill, color, val`) where the *required* attribute `val` is last.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TableCell({ shading: { type: ShadingType.CLEAR, color: "auto", fill: "EEEEEE" }, children })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
