# Fixture: demo-73-comments

Phase 40a port of `/opt/docx-ref/demo/73-comments.ts`.

Two paragraphs with comment markers (commentRangeStart/End +
commentReference). Demo defines four comments (ids 0-3); the
body's comment markers reference them. Comment 0's content
includes an ImageRun (deferred — we compare ONLY
`word/document.xml`, the comments.xml content lives elsewhere).

The third TextRun in each paragraph has the
`children: [new CommentReference(N)], bold: true` pattern —
exercises the new `commentReference(id, configure)` overload
that lets a marker run carry rPr formatting.

The original demo uses `new Date()` for comment timestamps; that
non-determinism is handled by the publish-side date-strip step
(`/opt/fixtures/strip-demo.mjs`) which rewrites `w:date` attrs
to a sentinel. Body XML doesn't carry dates, so the diff is
clean here regardless.

**Compared XML parts:** `word/document.xml`
