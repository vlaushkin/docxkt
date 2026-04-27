# Fixture: patcher-identity

**What this demonstrates:** the round-trip pipeline — read a
`.docx`, parse each XML part to a DOM, serialize it back, re-zip
— preserves the semantic XML payload across compared parts.

The input is a copy of the `hello-world` fixture (from `:core`).
The output is the SAME content; the test asserts XMLUnit
equivalence after the round-trip.

This is the smoke test that the entire `:patcher` infrastructure
works end-to-end before any actual patch logic ships in Phase 31+.

**Compared XML parts:**
- `word/document.xml`
- `[Content_Types].xml`
- `_rels/.rels`

**Modifications from raw extraction:**
- None. Both `input/` and `output/` are byte-identical copies of
  hello-world's parts.

**Notes:**
- Raw bytes after round-trip will NOT match the input — JAXP's
  serializer emits empty elements as `<x></x>` not `<x/>` and may
  reorder attributes. XMLUnit handles both differences. We never
  assert on raw byte equality for XML parts.
- The fixture exists separately from `core/src/test/resources/
  fixtures/hello-world/` rather than being symlinked because the
  patcher's resource layout (input/ + output/ subdirs) is
  intentionally distinct from the core's flat layout.
