# Fixture: paragraph-extras-combo

**What this demonstrates:** four scalar `<w:pPr>` extras in one
paragraph, locking the canonical emission positions for each:

```
<w:bidi/>                     <!-- bidirectional, OnOff true -->
<w:contextualSpacing/>        <!-- OnOff true, between ind and jc -->
<w:outlineLvl w:val="2"/>     <!-- after jc -->
<w:suppressLineNumbers/>      <!-- after outlineLvl -->
```

Note `<w:contextualSpacing>`'s position is non-obvious —
upstream emits it between `<w:ind>` and `<w:jc>`, *not* near the
other OnOffs at the top of `<w:pPr>`. This fixture's run has no
indent or alignment set, so the positional test is implicit; the
canonical order is still verified via the single-fixture emission
order `bidi → contextualSpacing → outlineLvl → suppressLineNumbers`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ bidirectional: true, contextualSpacing: true, outlineLevel: 2, suppressLineNumbers: true, children })`

**Compared XML parts:**
- `word/document.xml`
