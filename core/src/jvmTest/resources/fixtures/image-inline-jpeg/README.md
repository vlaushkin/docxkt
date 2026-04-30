# Fixture: image-inline-jpeg

**What this demonstrates:** same structure as `image-inline-png`
with a JPEG payload. Locks the format-dependent parts:

- `[Content_Types].xml` gets a `<Default Extension="jpg"
  ContentType="image/jpeg"/>` (we only compare `word/document.xml`
  here, but the MIME emission stays exercised on the happy path).
- The file on disk is `word/media/image1.jpg` (extension `jpg`,
  not `jpeg` — matches `ImageFormat.JPEG.extension`).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new ImageRun({ data: bytes, type: "jpg", transformation: { width: 100, height: 100 } })`

**Compared XML parts:**
- `word/document.xml`

**Compared binary parts:**
- `word/media/image1.jpg`

**Modifications from raw upstream extraction:**
- `r:embed` normalized to `rId1`.
- Filename rewritten from `<sha1>.jpg` to `image1.jpg`.
