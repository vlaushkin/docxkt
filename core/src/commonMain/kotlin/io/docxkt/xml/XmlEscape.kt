// No upstream analogue — dolanmiu/docx delegates XML escaping to the npm
// `xml` library. We inline it.
package io.docxkt.xml

/**
 * XML text and attribute-value escaping. Pure functions; always use these
 * — never hand-roll escape logic at call sites.
 */
internal object XmlEscape {
    fun escapeText(value: String): String {
        if (!needsTextEscape(value)) return value
        val out = StringBuilder(value.length + 8)
        for (ch in value) {
            when (ch) {
                '&' -> out.append("&amp;")
                '<' -> out.append("&lt;")
                '>' -> out.append("&gt;")
                else -> out.append(ch)
            }
        }
        return out.toString()
    }

    fun escapeAttributeValue(value: String): String {
        if (!needsAttrEscape(value)) return value
        val out = StringBuilder(value.length + 8)
        for (ch in value) {
            when (ch) {
                '&' -> out.append("&amp;")
                '<' -> out.append("&lt;")
                '>' -> out.append("&gt;")
                '"' -> out.append("&quot;")
                '\'' -> out.append("&apos;")
                '\t' -> out.append("&#x9;")
                '\n' -> out.append("&#xA;")
                '\r' -> out.append("&#xD;")
                else -> out.append(ch)
            }
        }
        return out.toString()
    }

    private fun needsTextEscape(value: String): Boolean {
        for (ch in value) {
            if (ch == '&' || ch == '<' || ch == '>') return true
        }
        return false
    }

    private fun needsAttrEscape(value: String): Boolean {
        for (ch in value) {
            when (ch) {
                '&', '<', '>', '"', '\'', '\t', '\n', '\r' -> return true
            }
        }
        return false
    }
}
