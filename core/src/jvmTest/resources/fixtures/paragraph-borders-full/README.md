# Fixture: paragraph-borders-full

**What this demonstrates:** all five sides of `<w:pBdr>` set —
top, bottom, left, right, between. Extends Phase 5's
`paragraph-bottom-border` fixture (which exercised only bottom)
and confirms the child-emission order `top → bottom → left →
right → between` matches upstream.

Wire:
```
<w:pBdr>
  <w:top    w:val="single" w:color="FF0000" w:sz="6"/>
  <w:bottom w:val="single" w:color="00FF00" w:sz="6"/>
  <w:left   w:val="single" w:color="0000FF" w:sz="6"/>
  <w:right  w:val="single" w:color="FFFF00" w:sz="6"/>
  <w:between w:val="single" w:color="auto" w:sz="4"/>
</w:pBdr>
```

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Paragraph({ border: { top, bottom, left, right, between }, children })`

**Compared XML parts:**
- `word/document.xml`
