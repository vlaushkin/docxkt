# Fixture: run-border-shading

**What this demonstrates:** run-level `<w:bdr>` + `<w:shd>`. Reuses
the Phase 4b `BorderSide` primitive (single-sided at the run level,
not the four-side block) and the Phase 4b `Shading` primitive
verbatim.

Locks:

- `<w:bdr>` attribute order `val, color, sz, space` (same as table/
  paragraph borders).
- `<w:shd>` attribute order `fill, color, val` (non-alphabetical, val
  last).
- `<w:bdr>` comes before `<w:shd>` inside `<w:rPr>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, border: { style: BorderStyle.SINGLE, size: 4, color: "FF0000" }, shading: { type: ShadingType.CLEAR, color: "auto", fill: "EEEEEE" } })`

**Compared XML parts:**
- `word/document.xml`
