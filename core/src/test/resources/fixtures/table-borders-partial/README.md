# Fixture: table-borders-partial

**What this demonstrates:** top + bottom set to THICK / size 16 /
#FF0000; the other four sides fall through to upstream's default fill
(SINGLE / size 4 / "auto"). Locks the `<w:tblBorders>` per-side
default-fill contract: unset sides still appear in the wire, with the
upstream defaults.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ borders: { top, bottom }, rows })` — only two sides
  specified; insideH / insideV / left / right left to upstream
  defaults.

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
