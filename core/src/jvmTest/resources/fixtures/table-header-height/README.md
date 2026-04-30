# Fixture: table-header-height

**What this demonstrates:** `<w:trPr>` with both properties Phase 4a
carries:

- `<w:tblHeader/>` — this row repeats as a header on every page the
  table crosses.
- `<w:trHeight w:val="500" w:hRule="atLeast"/>` — minimum row height
  of 500 twips, grows for tall content.

Locks child order inside `<w:trPr>` (tblHeader → trHeight) and the
attribute order on `<w:trHeight>` (val, hRule).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TableRow({ tableHeader: true, height: { value: 500, rule: HeightRule.ATLEAST }, children })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
