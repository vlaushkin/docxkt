# Fixture: header-multi-paragraph

**What this demonstrates:** a header carrying two paragraphs — the
content model `List<Paragraph>`, in order. Exercises iteration
inside `<w:hdr>`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Header({ children: [new Paragraph(...), new Paragraph(...)] })`

**Compared XML parts:**
- `word/document.xml`
- `word/header1.xml`

**Modifications from raw upstream extraction:**
- `<w:headerReference>` `r:id` rewritten to `rId1`.
