# Fixture: style-basedon-chain

**What this demonstrates:** third Phase 11 fixture. Two paragraph
styles coexist in the same document; the second (`Subheading`)
bases on the first (`Heading`). Key assertion: `<w:basedOn>` is
emitted inline (no flattening) and the two styles appear in
DSL-declaration order inside `word/styles.xml`.

- `word/document.xml` — two paragraphs, one per style reference.
- `word/styles.xml` — `<w:style w:styleId="Heading">` then
  `<w:style w:styleId="Subheading">`, each with its own `<w:basedOn>`
  (`Normal` and `Heading` respectively).

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**API calls:**
- `new Document({ styles: { paragraphStyles: [… × 2] }, ... })`
- `new Paragraph({ style, children })` × 2

**Compared XML parts:**
- `word/document.xml`
- `word/styles.xml`

**Modifications from raw upstream extraction:**
- `<w:docDefaults>` stripped.
- Every factory-shipped `<w:style>` stripped (including a
  colliding `Heading1` the user didn't declare).
- Last-occurrence-wins dedupe keeps user-declared `Heading` and
  `Subheading`.

**Invariant validated:** Phase 11 does NOT flatten `basedOn`
chains. The chain `Subheading → Heading → Normal` is resolved by
Word at render time from the wire pointers, not by our serializer.

**Publisher invocation:**
```
publish.mjs --keep-styles Heading,Subheading style-basedon-chain word/styles.xml
```
