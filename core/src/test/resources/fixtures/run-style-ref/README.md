# Fixture: run-style-ref

**What this demonstrates:** `<w:rStyle>` at the very front of
`<w:rPr>` plus three OnOff extras near the end:

```
<w:rStyle w:val="Heading1"/>          <!-- first child of rPr -->
<w:noProof/>                          <!-- OnOff true -->
<w:snapToGrid w:val="false"/>         <!-- OnOff false -->
<w:rtl/>                              <!-- rightToLeft, OnOff true -->
```

Exercises both ends of the canonical child order — rStyle is the
first child upstream emits, and `rtl` sits near the tail (before
`em` / `lang`).

**`rStyle` ships only the element emission.** Resolving `"Heading1"`
against a styles part is Phase 6 / Styles scope (still deferred).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, style: "Heading1", noProof: true, snapToGrid: false, rightToLeft: true })`

**Compared XML parts:**
- `word/document.xml`
