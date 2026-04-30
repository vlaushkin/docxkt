# Fixture: demo-9-images-in-header-and-footer

Phase 52 port. Image in default header AND default footer
(same image bytes, deduped — the binary lives once in
`word/media/{sha1}.gif` and is referenced from both
`word/_rels/header1.xml.rels` and
`word/_rels/footer1.xml.rels`).

Upstream demo 9 omits `type` on the ImageRun, which causes
upstream to emit `media/{sha1}.undefined` (literal string
"undefined"). The fixture-generator script adds an explicit
`type: "gif"` to fix this — every other byte matches upstream's
output verbatim.

**Compared XML parts:** auto-discovered.
