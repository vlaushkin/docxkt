# Fixture: demo-75-tab-stops

Phase 38 port of `/opt/docx-ref/demo/75-tab-stops.ts`.

A receipt-style document with three column-set tab stops:
two-column (one stop at MAX), and four-column (stops at
`MAX/4*2`, `MAX/4*3`, `MAX`). The middle stop is `6769.5`
(fractional) — closes the Phase 37b "fractional tab-stop
position" gap by upgrading `TabStop.position: Int → Number`.

The `defaultTabStop: 0` document setting and TextRun `\t` chars
in text content also exercised.

**Compared XML parts:** `word/document.xml`
