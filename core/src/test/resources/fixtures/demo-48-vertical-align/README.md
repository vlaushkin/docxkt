# Fixture: demo-48-vertical-align

Phase 39 port of `/opt/docx-ref/demo/48-vertical-align.ts`.

A single section centred vertically (`<w:vAlign w:val="center"/>`)
containing one paragraph that mixes plain, bold, and bold-with-leading-tab
runs. Re-port enabled by Phase 38's `RunScope.tab()` plus Phase 39's
new `SectionVerticalAlign` enum.

**Compared XML parts:** `word/document.xml`
