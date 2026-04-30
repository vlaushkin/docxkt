# Fixture: run-color-size

**What this demonstrates:** `<w:color w:val="3366CC"/>` and the
`<w:sz w:val="24"/><w:szCs w:val="24"/>` pair — color in hex RGB,
size in OOXML **half-points** (`24` = 12 pt), with the auto-mirrored
complex-script size.

**Why half-points matters:** the DSL must not multiply by two for
users' convenience. A real regression would be passing the user a
value-in-points API and silently doubling; this fixture would fail in
that world.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, color: "3366CC", size: 24 })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).
