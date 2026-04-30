# Fixture: style-character

**What this demonstrates:** second Phase 11 fixture. One
user-declared character style (`Emphasis`, italics rPr); one
paragraph with three runs, the middle one referencing the style
via `<w:rStyle w:val="Emphasis"/>`.

- `word/document.xml` — one paragraph, three runs:
  `See ` (plain) / `this` (with `<w:rPr><w:rStyle w:val="Emphasis"/></w:rPr>`)
  / ` for details.` (plain).
- `word/styles.xml` — one `<w:style w:type="character"
  w:styleId="Emphasis">` with `<w:name>`, the upstream-default
  `<w:uiPriority w:val="99"/>` + `<w:unhideWhenUsed/>`, and
  `<w:rPr>` with `<w:i/>` + `<w:iCs/>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ styles: { characterStyles: [{ id, name, run }] }, ... })`
- `new Paragraph({ children: [TextRun, TextRun({text, style}), TextRun] })`

**Compared XML parts:**
- `word/document.xml`
- `word/styles.xml`

**Modifications from raw upstream extraction:**
- `<w:docDefaults>` stripped.
- All factory-shipped `<w:style>` entries stripped. Only
  user-declared `Emphasis` survives.

**Notes on upstream defaults:**
- Upstream's `StyleForCharacter` constructor auto-applies
  `uiPriority: 99` and `unhideWhenUsed: true` when the caller
  hasn't explicitly set them. Our `StyleScope.buildCharacterStyle`
  matches — so emitted wire carries both elements even though the
  Kotlin DSL call sets neither.

**Publisher invocation:**
```
publish.mjs --keep-styles Emphasis style-character word/styles.xml
```
