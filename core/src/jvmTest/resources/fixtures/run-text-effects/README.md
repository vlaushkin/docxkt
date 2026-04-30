# Fixture: run-text-effects

**What this demonstrates:** five visual-effect fields in one run,
locking their canonical emission order inside `<w:rPr>`:

```
<w:emboss/>                    <!-- OnOff true -->
<w:imprint w:val="false"/>     <!-- OnOff false — w:val="false" -->
<w:vanish/>                    <!-- truthy-only emission -->
<w:effect w:val="shimmer"/>    <!-- TextEffect enum -->
<w:em w:val="dot"/>             <!-- EmphasisMark enum -->
```

Notable: `imprint = false` emits `<w:imprint w:val="false"/>` (full
OnOff), but `vanish = false` would have emitted nothing
(truthy-only quirk upstream preserves — we match).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new TextRun({ text, emboss: true, imprint: false, vanish: true, effect: TextEffect.SHIMMER, emphasisMark: { type: EmphasisMarkType.DOT } })`

**Compared XML parts:**
- `word/document.xml`
