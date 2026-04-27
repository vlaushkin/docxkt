# Fixture: demo-12-scaling-images

Phase 44 port of `/opt/docx-ref/demo/12-scaling-images.ts`.

Single section, "Hello World" paragraph followed by 4 paragraphs
each carrying an inline image (pizza.gif) at successively larger
sizes (50/100/250/400). Upstream dedupes by hash so all four
references share the same rId.

**Compared XML parts:** auto-discovered (Phase 42 default).
