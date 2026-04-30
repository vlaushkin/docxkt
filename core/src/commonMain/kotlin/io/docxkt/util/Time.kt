package io.docxkt.util

/**
 * Current UTC instant rendered as an ISO-8601 string. Used as the default
 * timestamp for `dcterms:created` / `dcterms:modified` in `docProps/core.xml`.
 * Per-platform precision varies (JVM emits nanoseconds, Apple Foundation
 * emits milliseconds); both are valid ISO-8601 and Word + LibreOffice
 * accept either. Fixture tests pin the value explicitly to keep the
 * regression net byte-stable.
 */
internal expect fun nowIso8601(): String
