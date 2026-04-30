# Fixture: table-vmerge

**What this demonstrates:** a vertical merge across two rows.

- Row 1, cell 1: `<w:vMerge w:val="restart"/>` + content "Tall".
- Row 1, cell 2: plain content "R1".
- Row 2, cell 1: `<w:vMerge w:val="continue"/>` with an empty `<w:p/>`
  (cells must end with a paragraph even when empty).
- Row 2, cell 2: plain content "R2".

Locks two conventions:
1. Upstream always emits `w:val` on `<w:vMerge>` — even the continue
   form (OOXML allows defaulting but upstream doesn't).
2. Empty cells still carry a terminating paragraph. Our DSL auto-pads.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TableCell({ verticalMerge: VerticalMergeType.RESTART, children })`
- `new TableCell({ verticalMerge: VerticalMergeType.CONTINUE, children: [] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
