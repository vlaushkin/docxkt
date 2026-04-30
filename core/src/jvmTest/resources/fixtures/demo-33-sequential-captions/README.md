# Fixture: demo-33-sequential-captions

Phase 39 port of `/opt/docx-ref/demo/33-sequential-captions.ts`.

Four paragraphs each containing two `<w:r>` complex fields with
`SEQ <name>` instructions (Caption / Label / Another). Each emits
the begin/instrText/separate/end chain with `w:dirty="true"` so
Word recomputes the values at open time.

Closes Phase 37a "SequentialIdentifier SEQ field" gap via the
new `paragraph { sequentialIdentifier(name) }` DSL method.

**Compared XML parts:** `word/document.xml`
