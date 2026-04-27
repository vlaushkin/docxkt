# Fixture: headers-all-three

**What this demonstrates:** the full set of header types
(default + first + even) in a single section. Locks:

- All three `<w:headerReference>` elements emit in upstream
  order `default → first → even` inside `<w:sectPr>`.
- `<w:titlePg/>` (after `<w:pgNumType/>`, before `<w:docGrid>`)
  enables the FIRST header.
- `<w:evenAndOddHeaders/>` in `word/settings.xml` enables the
  EVEN header.
- Sequential header parts: rId1→header1.xml (default),
  rId2→header2.xml (first), rId3→header3.xml (even).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. Re-publish with `publish.mjs
headers-all-three word/header1.xml word/header2.xml
word/header3.xml word/settings.xml`.

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml` / `header2.xml` / `header3.xml`
- `word/settings.xml`

**Modifications from raw upstream extraction:**
- Header reference `r:id` rewritten upstream-rId7/8/9 →
  `rId1`/`rId2`/`rId3`.
