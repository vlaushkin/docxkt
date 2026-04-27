# Fixture: patcher-image-replace

**What this demonstrates:** `Patch.Image` — `{{logo}}`
marker replaced by an inline image.

- Input runs: one paragraph with text
  `before {{logo}} after`. Plus a stub
  `word/_rels/document.xml.rels` (empty Relationships root).
- Patch: `{"logo" → Patch.Image(67-byte PNG, 952500x952500
  EMUs, ImageFormat.PNG)}`.
- Output:
  - `word/document.xml`: marker run split into prefix-run +
    drawing-run (with `r:embed="rId1"`) + suffix-run.
  - `[Content_Types].xml`: `<Default Extension="png"
    ContentType="image/png">` appended.
  - `word/_rels/document.xml.rels`: new image rel
    `rId1 → media/image1.png`.
  - `word/media/image1.png`: 67-byte 1×1 transparent PNG
    written verbatim.

The drawing element matches `:core`'s `Drawing.appendXml`
output verbatim (cross-checked against the
`image-inline-png` generation fixture).

**Compared XML parts:**
- `word/document.xml`
- `[Content_Types].xml`
- `word/_rels/document.xml.rels`

**Compared binary parts:**
- `word/media/image1.png`
