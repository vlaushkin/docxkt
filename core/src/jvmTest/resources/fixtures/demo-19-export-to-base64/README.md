# Fixture: demo-19-export-to-base64

Phase 45 port of `/opt/docx-ref/demo/19-export-to-base64.ts`.

Single section with one paragraph holding three formatted runs:
"Hello World", "Foo" (bold), and a tab + "Bar" (bold). The
upstream demo only differs from a regular Packer.toBuffer demo
in choosing toBase64String — the resulting XML is identical.

**Compared XML parts:** auto-discovered (Phase 42 default).
