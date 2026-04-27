# Fixture: demo-37-images-to-header-and-footer

Phase 52 port. Three image runs in default header (jpg, gif, jpg).
The two jpg runs share content so upstream emits one rId0 for
both — verifies same-part image dedup.

Hash-based media filenames have been normalized to image{N}.{ext}
in the same order they appear in the upstream rels file.

**Compared XML parts:** auto-discovered.
