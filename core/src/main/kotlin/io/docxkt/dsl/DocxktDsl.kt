// No upstream analogue — dolanmiu's public API is constructor-based
// (new Document({...})); our Kotlin DSL is a deliberate redesign.
package io.docxkt.dsl

/**
 * Marker applied to every DSL scope receiver. Prevents accidental
 * cross-scope calls (e.g. invoking paragraph { } inside run { }) by making
 * outer-scope members inaccessible without an explicit qualifier.
 */
@DslMarker
public annotation class DocxktDsl
