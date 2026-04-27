# Fixture: image-inline-png

**What this demonstrates:** an inline PNG image inside a run.

- `word/document.xml` — one paragraph with one run that contains a
  `<w:drawing>` tree ending in `<a:blip r:embed="rId1"/>`.
- `word/media/image1.png` — 72-byte 2x2 PNG payload. The fixture's
  binary comparison (`assertBytesEqual`) locks this to the same
  bytes the library emits.

The PNG bytes were committed directly under the fixture (as
`word/media/image1.png`) and are the same bytes `generate.mjs`
reads. Our library's `RunScope.image(bytes = ..., format = PNG)`
embeds those bytes verbatim, so the binary diff should pass
byte-for-byte.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new ImageRun({ data: bytes, type: "png", transformation: { width: 100, height: 100 } })`

**Compared XML parts:**
- `word/document.xml`

**Compared binary parts:**
- `word/media/image1.png`

**Modifications from raw upstream extraction:**
- `<a:blip r:embed>` normalized to our `rId1` (upstream emits `rId7`
  because of its many unrelated rIds). Publisher rewrites at
  extraction time.
- The upstream file at `word/media/<sha1>.png` is renamed to
  `word/media/image1.png` so our sequential-naming scheme matches.
  Bytes are identical.
