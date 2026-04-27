# Fixture: hyperlink-external-simple

**What this demonstrates:** first Phase 12 fixture. One paragraph
with three runs — middle one wrapped in `<w:hyperlink>` pointing
at an external URL via the first `TargetMode="External"`
relationship in the document.

- `word/document.xml` — `<w:p>` with `Visit` / hyperlink / ` for more.`
  runs. The hyperlink carries `w:history="1"` and `r:id="rId1"`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [...] })`
- `new Paragraph({ children: [TextRun, ExternalHyperlink, TextRun] })`
- `new ExternalHyperlink({ link, children: [TextRun] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- Hyperlink `r:id` rewritten from upstream's auto-allocated
  value (e.g. `rId6`) to our allocator's `rId1` — matches the
  header / footer / blip rId-remap already in place.

**Relationship wire (not diffed but noted here for future
  reference):**
```
<Relationship Id="rId1"
              Type=".../relationships/hyperlink"
              Target="https://example.com"
              TargetMode="External"/>
```
First non-internal relationship our output produces.
