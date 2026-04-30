// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.metadata.CoreProperties

/**
 * Configure document metadata that lands in `docProps/core.xml`
 * and `docProps/custom.xml`.
 *
 * All fields are optional. Unset fields fall through to upstream's
 * defaults (creator/lastModifiedBy = `"Un-named"`, revision = `1`,
 * timestamps = `Instant.now()`).
 */
@DocxktDsl
public class PropertiesScope internal constructor(
    internal val context: DocumentContext,
) {
    public var title: String? = null
    public var subject: String? = null
    public var creator: String? = null
    public var keywords: String? = null
    public var description: String? = null
    public var lastModifiedBy: String? = null
    public var revision: Int? = null

    /**
     * W3CDTF-formatted creation timestamp (e.g.
     * `"2026-04-24T00:00:00.000Z"`). Defaults to `Instant.now()`.
     */
    public var createdAt: String? = null

    /**
     * W3CDTF-formatted modification timestamp. Defaults to the same
     * value as [createdAt].
     */
    public var modifiedAt: String? = null

    /**
     * Add a user-defined custom property. Appears inside
     * `docProps/custom.xml` with a sequential `pid` starting at 2.
     */
    public fun custom(name: String, value: String) {
        context.registerCustomProperty(name, value)
    }

    internal fun build() {
        val base = if (createdAt != null) {
            CoreProperties(
                title = title,
                subject = subject,
                creator = creator,
                keywords = keywords,
                description = description,
                lastModifiedBy = lastModifiedBy,
                revision = revision,
                createdAt = createdAt!!,
                modifiedAt = modifiedAt ?: createdAt!!,
            )
        } else {
            CoreProperties(
                title = title,
                subject = subject,
                creator = creator,
                keywords = keywords,
                description = description,
                lastModifiedBy = lastModifiedBy,
                revision = revision,
            ).let {
                // Apply modifiedAt override if only it was set.
                if (modifiedAt != null) it.copy(modifiedAt = modifiedAt!!) else it
            }
        }
        context.coreProperties = base
    }
}
