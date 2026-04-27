# Fixture: hyperlink-external-styled

**What this demonstrates:** second Phase 12 fixture. Combines
Phase 11 (styles) with Phase 12 (hyperlinks). A character style
`Hyperlink` (blue color `0563C1`, single underline — upstream's
stock hyperlink look) is user-declared; a paragraph contains a
single hyperlink whose one run references the style via
`<w:rStyle w:val="Hyperlink"/>`.

- `word/document.xml` — paragraph with one hyperlink wrapping
  one styled run.
- `word/styles.xml` — single `<w:style w:type="character"
  w:styleId="Hyperlink">` entry with `<w:color/>` and `<w:u/>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ styles: { characterStyles: [{ id: "Hyperlink", name,
  run }] }, ... })`
- `new ExternalHyperlink({ link, children: [new TextRun({ text,
  style: "Hyperlink" })] })`

**Compared XML parts:**
- `word/document.xml`
- `word/styles.xml`

**Modifications from raw upstream extraction:**
- `<w:docDefaults>` stripped; every factory-shipped `<w:style>`
  stripped except the user-declared `Hyperlink` (which collides
  with upstream's factory `Hyperlink`; last-wins keeps user's).
- Hyperlink `r:id` rewritten to our allocator's `rId1`.

**Publisher invocation:**
```
publish.mjs --keep-styles Hyperlink hyperlink-external-styled word/styles.xml
```
