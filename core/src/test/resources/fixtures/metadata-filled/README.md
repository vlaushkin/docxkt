# Fixture: metadata-filled

**What this demonstrates:** second Phase 14 fixture. Every
`<cp:coreProperties>` child element emitted with a non-default
value — exercises the full DSL surface of `properties { }`.

- `docProps/core.xml` — emits `dc:title`, `dc:subject`,
  `dc:creator`, `cp:keywords`, `dc:description`,
  `cp:lastModifiedBy`, `cp:revision`, `dcterms:created`,
  `dcterms:modified`.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`.

**Compared XML parts:**
- `docProps/core.xml`

**Modifications from raw upstream extraction:**
- Publisher normalizes the two `dcterms:*` timestamps to
  `2026-04-24T00:00:00.000Z`; the Kotlin DSL test sets the same
  sentinel.
