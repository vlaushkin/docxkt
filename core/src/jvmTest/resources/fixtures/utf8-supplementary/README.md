# Fixture: utf8-supplementary

**What this demonstrates:** emoji `😀` (U+1F600, surrogate pair in
UTF-16) and non-BMP CJK `𠮷` (U+20BB7, surrogate pair in UTF-16)
round-trip through Kotlin `String` → UTF-8 → `<w:t>` → ZIP →
`word/document.xml`. **This is the port's raison d'être — POI breaks
on surrogate pairs.**

If this fixture fails the bug lives in one of:
- `XmlEscape` — must not split or re-encode surrogate halves.
- `DocxPackager` — the ZIP writer's charset handling.
- `MainDocumentPart.toBytes` — `.toByteArray(Charsets.UTF_8)` must
  handle surrogate pairs correctly (Kotlin's stdlib does by
  default; if the bug lives here, we've built it on top of broken
  charset handling somewhere else).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun("Hello \u{1F600}")` — smiley emoji.
- `new TextRun("\u{20BB7}")` — non-BMP CJK (yoshi).

**Compared XML parts:**
- `word/document.xml`

**Modifications from raw upstream extraction:**
- None.
