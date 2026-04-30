# Fixture: demo-95-style-shading-borders

Phase 37 port of `/opt/docx-ref/demo/95-paragraph-style-with-shading-and-borders.ts`.

A paragraph style is registered by upstream with shading +
borders, and a body paragraph references it. We compare ONLY
`word/document.xml` (the body) — the styles.xml definition
is upstream's territory and doesn't need to match ours.

**Compared XML parts:** `word/document.xml`
