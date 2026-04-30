# Fixture: demo-92-declarative-custom-fonts

Phase 39 port of `/opt/docx-ref/demo/92-declarative-custom-fonts.ts`.

A single paragraph with three runs (plain "Hello World", bold+size=40
"Foo Bar", bold-with-tab "Github is the best"). Upstream sets the
default document font ("Pacifico") in styles.xml — body XML carries
no font reference. Body matches our simpler rendering.

The actual font binary embedding (fontTable.xml + word/fonts/) is
not modelled; we compare ONLY `word/document.xml`.

**Compared XML parts:** `word/document.xml`
