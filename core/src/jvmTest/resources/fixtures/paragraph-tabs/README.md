# Fixture: paragraph-tabs

**What this demonstrates:** three tab stops with mixed types, one
carrying a leader character:

```
<w:tabs>
  <w:tab w:val="left"   w:pos="2000"/>
  <w:tab w:val="center" w:pos="4000"/>
  <w:tab w:val="right"  w:pos="9000" w:leader="dot"/>
</w:tabs>
```

Locks:
- `<w:tab>` attribute order `val, pos, leader` (leader omitted when
  null).
- Tab-stop list preserves insertion order inside `<w:tabs>`.
- `TabStopType.RIGHT` and `TabLeader.DOT` enum → wire mapping.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ tabStops: [{ type, position, leader? }, ...], children })`

**Compared XML parts:**
- `word/document.xml`
