# Fixture: image-with-description

**What this demonstrates:** a PNG image with a `descr` attribute on
`<wp:docPr>` — the accessible alt-text Word screen-readers announce.
Locks the description-emission path: our `Image.description` flows
into `Drawing.description` flows into `wp:docPr/@descr`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new ImageRun({ data, type: "png", transformation, altText: { description: "a tiny red square" } })`

**Compared XML parts:**
- `word/document.xml`

**Compared binary parts:**
- `word/media/image1.png`

**Modifications from raw upstream extraction:**
- `r:embed` normalized to `rId1`.
- Filename rewritten from `<sha1>.png` to `image1.png`.
