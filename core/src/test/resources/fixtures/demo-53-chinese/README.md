# Fixture: demo-53-chinese

Phase 37b port of `/opt/docx-ref/demo/53-chinese.ts`.

Four paragraphs: a Heading1, two short Chinese strings (one
plain, one with `eastAsia="SimSun"`), and a long template-literal
blob with embedded newlines + indenting whitespace. Default-font
setup lives in `word/styles.xml` (not compared).

The long paragraph is loaded from `lastparagraph.txt` (extracted
verbatim from upstream's body) so the literal newlines + spaces
round-trip without escaping headaches in Kotlin source.

**Compared XML parts:** `word/document.xml`
