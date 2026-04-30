# Fixture: section-default

**What this demonstrates:** the default `<w:sectPr>` block upstream
emits for a trivial document — A4 portrait
(`pgSz w=11906 h=16838 orient=portrait`), default margins
(`pgMar top/right/bottom/left=1440 header/footer=708 gutter=0`),
and the two always-present siblings `<w:pgNumType/>` and
`<w:docGrid w:linePitch="360"/>`.

The test proves our library's byte-for-byte match against upstream
for a document that configures *nothing* about the section. This is
the correctness proof for the Phase 5 unconditional-default design.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ sections: [{ properties: {}, children: [...] }] })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None. Phase 5 publisher passes upstream's output through.
