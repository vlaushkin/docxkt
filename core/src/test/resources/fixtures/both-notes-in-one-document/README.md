# Fixture: both-notes-in-one-document

**What this demonstrates:** Phase 17 stretch fixture. One
footnote + one endnote in the same document — asserts that
footnotes.xml and endnotes.xml are both emitted as distinct
parts, that the document rels include both
`FOOTNOTES` and `ENDNOTES` entries, and that the body
references work for both note kinds in the same paragraph
stream.

**Compared XML parts:**
- `word/document.xml`
- `word/footnotes.xml`
- `word/endnotes.xml`
