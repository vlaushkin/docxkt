# Fixture: table-width-pct

**What this demonstrates:** `<w:tblW w:type="pct" w:w="5000%"/>` —
percentage width. Locks two conventions:

1. OOXML percent values are **fiftieths of a percent**: `5000` = 100%.
2. Upstream emits the value with a literal `%` suffix in the XML — not
   just the number. Our `TableWidth.pct(5000)` → wire `"5000%"` mirrors
   this exactly.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Table({ width: { size: 5000, type: WidthType.PERCENTAGE }, ... })`

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
