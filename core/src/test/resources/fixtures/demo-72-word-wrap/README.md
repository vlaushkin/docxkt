# Fixture: demo-72-word-wrap

Phase 43 port of `/opt/docx-ref/demo/72-word-wrap.ts`.

Single section, four paragraphs. First two enable wordWrap = true,
last two leave it default. Each paragraph has CJK + a long
unbreakable digit run, exercising the `<w:wordWrap/>` paragraph
property.

**Compared XML parts:** auto-discovered (Phase 42 default).
