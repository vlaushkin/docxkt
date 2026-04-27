# docxkt — Claude Code operational guide

Kotlin/JVM port of [dolanmiu/docx](https://github.com/dolanmiu/docx),
a TypeScript library for generating Microsoft Word `.docx` files.
This file tells you **how to work** in this codebase. Public-facing
description is in `README.md`.

## Environment layout (inside container)

- `/workspace/` — the docxkt project. Read/write. You edit here.
- `/opt/docx-ref/` — full copy of dolanmiu/docx baked into the
  image: sources (`src/`), installed dependencies (`node_modules/`),
  and the compiled build (`dist/`). Both **readable** (for
  studying implementation in `src/`) and **executable** (generate
  fixtures with `require('/opt/docx-ref')`). Treat as conceptually
  read-only — any edits there won't survive container restarts.
- `/opt/fixtures/` — fixture-generator scripts (Node.js). Write
  here.

The dolanmiu/docx version in `/opt/docx-ref/` is pinned at
image-build time. Check its commit via the
`io.docxkt.docx-ref-sha` image label, or
`git -C /opt/docx-ref rev-parse HEAD` inside the container.

If any of these paths is missing, stop and ask. Do not try to
recreate them; the container is the ground truth.

## Architectural invariants (do not violate)

- `:core` is **pure Kotlin/JVM**, stdlib-only at runtime. No
  Android APIs, no Kotlin Multiplatform, no non-stdlib runtime
  dependencies. Test deps are fine: JUnit 5, XMLUnit, kotlin.test.
- `:patcher` is the only module allowed to use `javax.xml.parsers`
  / `javax.xml.stream` / `org.w3c.dom`, and only on the read side
  (parsing existing `.docx` ZIPs). Emission is still hand-rolled.
- `:android` is a thin Android wrapper. Depends on `:core` via
  `api(...)`. AndroidX Core is allowed; nothing else.
- **Every XML-producing class implements
  `appendXml(out: Appendable): Unit`** and writes its tag directly
  to the `Appendable`. No intermediate object tree (we
  intentionally diverge from upstream's `prepForXml` /
  `IXmlableObject` pattern). Do **not** introduce a DOM library,
  `kotlinx.serialization`, or any tree-based XML abstraction.
- Use `XmlComponent` / `IgnoreIfEmptyXmlComponent` base classes
  from `io.docxkt.xml`. Properties containers (`w:rPr`, `w:pPr`,
  etc.) extend `IgnoreIfEmptyXmlComponent` so they disappear when
  unset — never emit empty `<w:rPr/>` / `<w:pPr/>` tags.
- Public API is a **DSL** with `@DslMarker`. Internal state is
  immutable data classes / sealed hierarchies.
- No reflection, no annotation processing, no codegen in `:core`.
  It must compile and run on a vanilla JVM with no setup beyond
  Gradle.
- Namespace URIs and prefixes live as constants in
  `io.docxkt.xml.Namespaces` — single source of truth. Do not
  re-declare URIs inline.
- Twips/EMU measurement constants live in `io.docxkt.measure` —
  use `Twips.ONE_INCH` etc. instead of bare literals.
- Element-emission helpers live in `io.docxkt.xml.Elements`
  (`startElement`, `endElement`, `selfClosingElement`,
  `element { … }`). Use them — do not hand-write
  `out.append("<w:tag>")` literals.

## The golden fixture workflow

Validation is by byte-for-byte XML diff against fixtures generated
by upstream. Loop:

1. Write a fixture script under `/opt/fixtures/<feature>.mjs` that
   uses dolanmiu/docx to produce a minimal `.docx`. One feature
   per script — variants get separate scripts, not extra
   paragraphs.
2. Run it inside the sandbox, unzip the output, and copy the
   relevant XML parts into
   `core/src/test/resources/fixtures/<feature>/`. Do **not**
   commit parts the test does not assert against — it creates the
   illusion of coverage we don't have.
3. Write the Kotlin equivalent via the `document { … }` DSL.
4. Subclass `DocxFixtureTest` (or `PatcherFixtureTest` for
   patcher tests). The harness auto-discovers compared parts and
   admits per-test `expectedDivergences` for legitimate
   minimum-part-output differences from upstream's always-emit
   behaviour.
5. Run the test until the XMLUnit diff is green
   (`ignoreWhitespace`, `byNameAndAllAttributes`,
   `checkForIdentical`).
6. Commit.

`/opt/fixtures/` contents are not committed; only the extracted
XML lands in the repo. Pin the upstream SHA in the fixture's
`README.md` if a phase regenerates fixtures against a newer image.

## Upstream attribution

Every `.kt` file under `core/src/main/kotlin/io/docxkt/` whose
contents port behaviour from dolanmiu/docx carries a comment on
its primary declaration:

```kotlin
// Port of: src/file/paragraph/run/run.ts (L169-L223)
internal class Run(...) : XmlComponent("w:r") { ... }
```

Rules:

- Use the path **relative to the `src/` root** of dolanmiu/docx.
- Cite the line range of the upstream declaration you ported, not
  the whole file. If you pulled bits from multiple files, list
  them all.
- Mention the commit SHA only if the behaviour depends on
  something that may have been recently changed. The image-level
  SHA in `io.docxkt.docx-ref-sha` is authoritative otherwise.
- Files with no upstream analogue (DSL scopes, test harness,
  utilities we invented) carry a one-line note saying so — do
  not cite arbitrary upstream files as loose inspiration.

Reviewers will reject a new file that lacks the attribution
comment.

## OOXML gotchas that bite

- **OnOff boolean semantics.** Many `<w:*>` elements follow an
  unusual rule: `<w:b/>` with no attribute means `true`,
  `<w:b w:val="false"/>` means `false`, and the element's absence
  means "inherit / not set". Use the `OnOff` helper that emits
  the correct form for `Boolean?` and skips when `null`. Never
  emit `<w:b w:val="true"/>` — valid XML but not what upstream
  produces, and your fixture will diff red.
- **`xml:space="preserve"` on `<w:t>`.** Always emit it. Upstream
  does. Without it, Word collapses leading/trailing whitespace.
- **Namespace declarations live on the part root.** `<w:document>`
  carries `xmlns:w`, `xmlns:r`, `xmlns:mc`, etc. Don't re-declare
  on `<w:body>`.
- **Properties containers must be first.** `<w:rPr>` is the first
  child of `<w:r>`, `<w:pPr>` is the first child of `<w:p>`.
  Enforce order on the parent's `appendXml`.
- **Attribute order matters for fixture diffs.** Examples:
  `<w:shd>` is `fill, color, val` (val last despite being
  required); `ParagraphBorders` emits `top → bottom → left →
  right → between` matching upstream, NOT XSD order.
- **Upstream's test style doesn't port.** Their unit tests assert
  on an `IXmlableObject` tree we don't produce. Don't try to
  recreate the tree to make their tests copy-paste.

## Commit discipline

- **Commit after every green fixture / atomic change.** Not at
  the end of the session. Commits are recovery points if the
  session hits a limit mid-task.
- Commit messages: `feat(<module>): <what>`,
  `fix(<module>): <what>`, `test(<module>): <what>`,
  `refactor(<module>): <what>`, `docs: <what>`.
- **Never force-push, never rebase.** Linear commit history.
- Never touch remotes. No `git push` unless the user explicitly
  asks. Local commits only.

## Comment hygiene

- `// Port of: src/...:Lx-Ly` upstream attribution — required, do
  not remove.
- WHY comments documenting non-obvious OOXML quirks, upstream-bug
  workarounds, hidden invariants — keep.
- User-facing KDoc on public DSL surface — keep, examples
  encouraged.
- **Drop:** dev-journal artifacts (phase numbers, session refs,
  commit-style remarks, "rewritten in N.x" history,
  restate-the-name KDoc).

## Things NOT to do

- Do not copy TypeScript 1:1 into Kotlin. Kotlin idioms beat
  textual fidelity at the implementation-style level: `sealed
  class` instead of class hierarchy where a `when` is cleaner,
  `data class` instead of options-interface,
  `@DslMarker`-protected scope receivers instead of constructor
  chains. **Idioms apply at implementation-style only — they do
  NOT mean omitting features upstream supports.** If upstream has
  a field, our model has it. If upstream emits an attribute, we
  do. If a feature in `/opt/docx-ref/src/` exists, we
  transliterate it.
- Do not add dependencies to `:core` runtime. Stdlib only.
- Do not "clean up" unfamiliar files in `/workspace`. If a file
  looks weird and isn't obviously yours, leave it alone.
- Do not run `rm -rf` with unquoted globs or interpolated paths.
  Expand paths explicitly.
- Do not attempt to publish artifacts, push to remote, or touch
  GitHub.

## Conventions recap

- Kotlin 2.3.20, JVM toolchain 21, `explicitApi()` enabled in
  `:core` and `:patcher`.
- AGP 8.13.x, `minSdk 23`, `compileSdk 36` for `:android`.
- Test framework: JUnit 5 via `useJUnitPlatform()`.
- XML diffing: XMLUnit (`xmlunit-core`, `xmlunit-matchers`).
- Package root: `io.docxkt`.
- Fixture path: `core/src/test/resources/fixtures/<feature>/`
  (and `patcher/src/test/resources/fixtures/<feature>/`).
- Serialize method: `appendXml(out: Appendable): Unit`. No
  intermediate tree. Always write through `XmlEscape`,
  `Elements`, and `Namespaces` constants.
- Every ported class carries a `// Port of: src/...:Lx-Ly`
  comment.
