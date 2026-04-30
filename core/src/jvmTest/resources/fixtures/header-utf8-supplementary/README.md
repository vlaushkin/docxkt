# Fixture: header-utf8-supplementary

**Insurance fixture.** Mirrors Phase 5's `utf8-supplementary` but
exercises the **header code path**. Phase 5 locked UTF-8
supplementary-plane round-tripping through the body (`<w:t>` inside
`<w:document>`). Phase 6 introduces new emission code for
`word/header1.xml`; this fixture locks that header emission also
round-trips surrogate pairs.

Runs inside the header:
- `"Caption 😀"` (U+1F600 — supplementary-plane emoji)
- `"𠮷"` (U+20BB7 — non-BMP CJK, two-surrogate)

If this fixture fails, the header emission path somewhere mangles
UTF-16 surrogate pairs on the way to UTF-8 — the `XmlEscape` helper
is shared with the body, but `HeaderPart.toBytes()` and the ZIP
writer's charset handling are the likely suspects.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- Header with one paragraph, two text runs carrying
  supplementary-plane characters.

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`

**Modifications from raw upstream extraction:**
- `<w:headerReference>` `r:id` rewritten to `rId1`.
