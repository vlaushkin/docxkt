# Fixture: section-columns-with-separator

**What this demonstrates:** `<w:cols>` with `separate=true`
emitting `w:sep="true"` attribute. Locks the attribute order
including the `sep` slot:

```xml
<w:cols w:space="720" w:num="2" w:sep="true" w:equalWidth="true"/>
```

Order: `space → num → sep → equalWidth`.

**Compared XML parts:**
- `word/document.xml`
