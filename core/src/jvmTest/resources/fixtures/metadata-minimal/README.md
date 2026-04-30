# Fixture: metadata-minimal

**What this demonstrates:** first Phase 14 fixture. A minimal
document with no `properties { }` / `settings { }` DSL calls;
the five always-on metadata parts still emit with upstream-default
content.

- `docProps/core.xml` — `dc:creator` and `cp:lastModifiedBy` both
  default to `"Un-named"`; `cp:revision` to `"1"`; timestamps set
  to the `2026-04-24T00:00:00.000Z` sentinel by the publisher /
  test harness.
- `docProps/app.xml` — empty `<Properties>` root.
- `word/settings.xml` — `<w:displayBackgroundShape/>`,
  `<w:evenAndOddHeaders w:val="false"/>`,
  `<w:compat><w:compatSetting w:val="15" .../></w:compat>`.
- `word/fontTable.xml` — empty `<w:fonts/>` root.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`. `node <this-path>/generate.mjs /tmp/out.docx`.

**Compared XML parts:**
- `docProps/core.xml`
- `docProps/app.xml`
- `word/settings.xml`
- `word/fontTable.xml`

**Modifications from raw upstream extraction:**
- `docProps/core.xml`: publisher rewrites `dcterms:created` and
  `dcterms:modified` bodies to the fixed sentinel
  `2026-04-24T00:00:00.000Z` so golden diffs stay stable across
  fixture regenerations. Matching the Kotlin DSL test which sets
  `createdAt = "2026-04-24T00:00:00.000Z"`.
