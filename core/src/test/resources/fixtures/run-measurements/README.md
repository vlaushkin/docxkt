# Fixture: run-measurements

**What this demonstrates:** all four numeric `<w:rPr>` fields set
simultaneously. Locks the canonical emission order:

```
<w:spacing w:val="20"/>   <!-- characterSpacing (twips, signed) -->
<w:w w:val="150"/>         <!-- scale (percent) -->
<w:kern w:val="28"/>       <!-- kerning (half-points) -->
<w:position w:val="6pt"/>  <!-- position (universal measure, pass-through) -->
```

Unit conventions:
- `characterSpacing`: twips, signed.
- `scale`: percentage integer (`100` = 100%).
- `kern`: half-points.
- `position`: universal measure string; upstream passes through
  unparsed — `"6pt"`, `"-3pt"`, `"120"` all valid.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, characterSpacing: 20, scale: 150, kern: 28, position: "6pt" })`

**Compared XML parts:**
- `word/document.xml`
