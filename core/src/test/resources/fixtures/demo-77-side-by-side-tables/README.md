# Fixture: demo-77-side-by-side-tables

Phase 39 port of `/opt/docx-ref/demo/77-side-by-side-tables.ts`.

A 1×2 outer table with `bordersAllNone()` containing two 2×2
inner tables side-by-side. Closes Phase 37b "nested tables in
cells" gap. Each cell containing a nested table auto-pads with
an empty `<w:p/>` per OOXML's "cells must end in a paragraph"
rule.

**Compared XML parts:** `word/document.xml`
