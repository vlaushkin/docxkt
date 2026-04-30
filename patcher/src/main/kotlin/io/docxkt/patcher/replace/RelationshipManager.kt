// Port of: src/patcher/relationship-manager.ts
package io.docxkt.patcher.replace

import io.docxkt.xml.Namespaces
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.childNodes
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.localName

/**
 * Manipulates `word/_rels/document.xml.rels` (or any Relationships
 * file). The root is `<Relationships>`; children are
 * `<Relationship Id="rId{N}" Type="…" Target="…"/>`.
 */
internal object RelationshipManager {

    /** Image relationship type URI. Aliased for short call sites. */
    const val IMAGE_TYPE = Namespaces.REL_IMAGE

    /** Hyperlink relationship type URI. Aliased for short call sites. */
    const val HYPERLINK_TYPE = Namespaces.REL_HYPERLINK

    /**
     * Return the next available `rId` number. Scans existing
     * Relationship children for the highest `rId{N}` and returns
     * `N + 1`. Returns 1 if no relationships exist.
     */
    fun nextRid(doc: Document): Int {
        val root = doc.documentElement!!
        val children = root.childNodes
        var maxId = 0
        for (i in 0 until children.length) {
            val n = children.item(i) ?: continue
            if (n !is Element) continue
            if (n.localName == "Relationship") {
                val id = n.getAttribute("Id") ?: continue
                if (id.startsWith("rId")) {
                    val numStr = id.substring(3)
                    val num = numStr.toIntOrNull() ?: continue
                    if (num > maxId) maxId = num
                }
            }
        }
        return maxId + 1
    }

    /**
     * Append a `<Relationship Id="rId{id}" Type="{type}"
     * Target="{target}"/>` element. Optional [targetMode] sets
     * `TargetMode="External"` (used by hyperlink rels).
     */
    fun addRelationship(
        doc: Document,
        id: Int,
        type: String,
        target: String,
        targetMode: String? = null,
    ) {
        val root = doc.documentElement!!
        val rel = doc.createElementNS(Namespaces.PACKAGE_RELATIONSHIPS_NAMESPACE, "Relationship")
        rel.setAttribute("Id", "rId$id")
        rel.setAttribute("Type", type)
        rel.setAttribute("Target", target)
        if (targetMode != null) rel.setAttribute("TargetMode", targetMode)
        root.appendChild(rel)
    }

    /**
     * Inspect existing `Target="media/image{N}.{ext}"` entries and
     * return the highest N seen (0 if none).
     */
    fun maxImageMediaIndex(doc: Document): Int {
        val root = doc.documentElement!!
        val children = root.childNodes
        var maxIdx = 0
        val regex = Regex("""media/image(\d+)\.""")
        for (i in 0 until children.length) {
            val n = children.item(i) ?: continue
            if (n !is Element) continue
            if (n.localName == "Relationship") {
                val target = n.getAttribute("Target") ?: continue
                regex.find(target)?.let {
                    val num = it.groupValues[1].toIntOrNull() ?: return@let
                    if (num > maxIdx) maxIdx = num
                }
            }
        }
        return maxIdx
    }
}
