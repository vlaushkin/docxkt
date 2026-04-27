# Fixture: paragraph-combo

**What this demonstrates:** two paragraphs locking Phase 3's
correctness in one fixture.

1. A paragraph with alignment (`JUSTIFIED`), indent, spacing, and
   keepNext — verifies canonical `<w:pPr>` child order
   (`keepNext → spacing → ind → jc`) is exactly upstream's.
2. A plain paragraph with no properties — verifies the
   `IgnoreIfEmptyXmlComponent` suppression still holds when Paragraph
   has a non-null-only properties slot.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ alignment, indent, spacing, keepNext, children })`
- `new Paragraph({ children })` — no paragraph-level formatting.

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- `<w:sectPr>…</w:sectPr>` stripped (Phase 7 scope).

**Note:** `JUSTIFIED` emits `<w:jc w:val="both"/>` — the OOXML token is
`"both"`, matching upstream's `AlignmentType.JUSTIFIED` wire value.
