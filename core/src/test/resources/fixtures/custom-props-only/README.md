# Fixture: custom-props-only

**What this demonstrates:** third Phase 14 fixture. Two user-
defined custom properties. Asserts `docProps/custom.xml`
emission, the `vt:lpwstr` value wire, and the
`rId4 = customProperties` routing inside `_rels/.rels`.

- `docProps/custom.xml` — `<Properties>` root with two
  `<property>` children; `pid` counter starts at 2 (upstream
  convention) and increments.
- `_rels/.rels` — four relationships in order
  officeDocument → coreProperties → extendedProperties →
  customProperties.

**dolanmiu/docx SHA:** `9439c73871e3ac9af5a5889978b7fbea9f3b6a2f`

**Script:** `generate.mjs`.

**Compared XML parts:**
- `docProps/custom.xml`
- `_rels/.rels`

**Modifications from raw upstream extraction:**
- None; custom.xml and _rels/.rels content match upstream
  byte-for-byte.
