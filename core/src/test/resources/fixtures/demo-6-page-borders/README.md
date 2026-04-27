# Fixture: demo-6-page-borders

Phase 38 port of `/opt/docx-ref/demo/6-page-borders.ts`.

Re-port after Phase 38 closure of the Tab-in-formatted-run gap.
The demo is misnamed — it doesn't actually configure page borders;
it sets full-bleed page margins (0/0/0/0) and emits four
paragraphs (one with `tab()` inside a formatted run, one with
`Heading1` style reference, two plain).

**Compared XML parts:** `word/document.xml`
