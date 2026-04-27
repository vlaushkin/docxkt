# Fixture: run-bold-italic

**What this demonstrates:** the OnOff semantics of `<w:b>` / `<w:i>` —
attribute-free `<w:b/>` when `true`, `<w:b w:val="false"/>` when `false`.
Also covers the complex-script mirrors (`w:bCs`, `w:iCs`) upstream
emits whenever `bold` / `italics` is set.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs` in this folder. Run inside the sandbox:
`node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text: "...", bold: true, italics: true })` — attribute-free form.
- `new TextRun({ text: "...", bold: false })` — `w:val="false"` form.

**Compared XML parts:**

- `word/document.xml`

**Not compared:** Content_Types and package rels are unchanged from
Phase 1; Phase 2 does not re-diff them. All other ZIP entries upstream
emits are out of scope — see Phase 1's hello-world fixture README for
the full list.

**Modifications from raw upstream extraction:**

- `<w:sectPr>…</w:sectPr>` stripped (section properties are Phase 7
  scope). No other edits.
