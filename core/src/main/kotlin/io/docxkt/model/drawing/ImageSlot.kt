// No upstream analogue — upstream resolves rIds at the prepForXml
// pass (a second tree traversal). We use a shared mutable slot so
// Drawing can defer embed-rId resolution until Document assembly.
package io.docxkt.model.drawing

/**
 * A mutable rId placeholder for a [Drawing] that hasn't been given
 * its relationship id yet.
 *
 * The DSL registers an image at call time (inside `RunScope.image`)
 * before it knows whether the document also has headers or footers.
 * `Document.buildDocument()` runs the [io.docxkt.part.RelationshipAllocator]
 * in a fixed order (header, footer, images...) and fills in every
 * slot's [resolvedRid] before any part emits XML. The [Drawing]
 * component reads [resolvedRid] at emit time.
 *
 * Trying to serialize a [Drawing] whose slot is unresolved is a bug;
 * the getter-accessor below throws with a clear message.
 */
internal class ImageSlot {
    var resolvedRid: String? = null

    val rid: String
        get() = resolvedRid
            ?: error("ImageSlot was serialized before Document.buildDocument() resolved rIds")
}
