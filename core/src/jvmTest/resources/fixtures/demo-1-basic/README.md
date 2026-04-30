# Fixture: demo-1-basic

Phase 38 port of `/opt/docx-ref/demo/1-basic.ts`.

Three runs in one paragraph:
- "Hello World" plain
- "Foo Bar" bold + size 40
- `<w:tab/>` then "Github is the best", both bold (single run with
  leading tab — exercises `RunScope.tab()` from Phase 38).

**Compared XML parts:** `word/document.xml`
