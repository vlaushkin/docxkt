# Fixture: demo-76-compatibility

Phase 43 port of `/opt/docx-ref/demo/76-compatibility.ts`.

Body has one paragraph with "Hello World". The interesting part is
the document's compatibility section: 60+ legacy compat flags
(`<w:compatSetting>` is the modern form; the older `<w:compat>`
flags are emitted as element peers, e.g. `<w:useSingleBorderforContiguousCells/>`).

**Compared XML parts:** auto-discovered. Body matches; settings.xml
will diverge if compat flags aren't modelled.
