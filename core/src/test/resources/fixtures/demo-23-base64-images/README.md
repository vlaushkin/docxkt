# Fixture: demo-23-base64-images

Phase 44 port of `/opt/docx-ref/demo/23-base64-images.ts`.

Single section, six paragraphs:
- "Hello World" + parrots.bmp inline (100×100)
- image1.jpeg (100×100)
- dog.png (100×100)
- cat.jpg (100×100)
- parrots.bmp again — dedupes to first rId via Phase 44 dedup
- buffer.png (decoded base64) (100×100)

Five unique images, one dedup. Image bytes preserved alongside.

**Compared XML parts:** auto-discovered (Phase 42 default).
