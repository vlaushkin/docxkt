# Fixture: demo-51-character-styles

Phase 37b port of `/opt/docx-ref/demo/51-character-styles.ts`.

Two paragraphs reference custom character styles by id
(`myRedStyle`, `strong`) via `<w:rStyle w:val="..."/>`. The
style definitions in `word/styles.xml` are upstream's territory;
we compare ONLY `word/document.xml`.

**Compared XML parts:** `word/document.xml`
