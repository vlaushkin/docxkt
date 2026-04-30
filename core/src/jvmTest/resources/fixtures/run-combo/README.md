# Fixture: run-combo

**What this demonstrates:** three runs that together lock the harder
cases of `<w:rPr>`:

1. `strike + smallCaps + superScript` — multiple OnOffs plus
   `<w:vertAlign>`; verifies canonical child order
   (`smallCaps → strike → vertAlign`).
2. Plain run, no formatting — verifies `IgnoreIfEmptyXmlComponent`
   suppresses an empty `<w:rPr/>` instead of emitting one.
3. `allCaps = false` — exercises the OnOff false form on the
   `<w:caps>` element (the OOXML name for allCaps).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, strike: true, smallCaps: true, superScript: true })`
- `new TextRun({ text })` — no formatting.
- `new TextRun({ text, allCaps: false })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
