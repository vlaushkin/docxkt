# Fixture: demo-74-nodejs-stream

Phase 39 port of `/opt/docx-ref/demo/74-nodejs-stream.ts`.

Identical body to demo-1: three runs (plain "Hello World", bold
"Foo Bar", bold-with-leading-tab "Github is the best"). Demo's
unique aspect is `Packer.toStream(doc)` instead of toBuffer; the
.docx body is the same. Re-port enabled by Phase 38's
`RunScope.tab()`.

**Compared XML parts:** `word/document.xml`
