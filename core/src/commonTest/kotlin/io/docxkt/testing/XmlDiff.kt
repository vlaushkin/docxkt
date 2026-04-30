// Hand-rolled multiplatform replacement for XMLUnit's
// `DiffBuilder.compare(...).ignoreWhitespace()
// .withNodeMatcher(DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
// .checkForIdentical()` configuration. JVM keeps using XMLUnit in
// jvmTest as the canonical regression net; this normalizer runs in
// commonTest so iOS/macOS targets validate fixture-equivalence too.
package io.docxkt.testing

import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.xmlStreaming

internal object XmlDiff {

    /**
     * Compare [expected] and [actual] for structural equivalence:
     * - Inter-element whitespace skipped.
     * - Elements matched by `(namespaceURI, localName)`.
     * - Attribute SET equality (xmlns / xmlns:* declarations excluded).
     * - Strict child order.
     * - Text content COALESCED across consecutive TEXT / ENTITY_REF /
     *   CDSECT events before comparison — necessary because xmlutil
     *   readers can split a logical text node around entity references
     *   (`Alice&apos;s note.` may surface as three TEXT events on one
     *   side and one TEXT event on the other depending on parser
     *   internals).
     */
    fun diff(expected: String, actual: String): String? {
        val expectedTokens = tokenize(expected)
        val actualTokens = tokenize(actual)
        val path = ArrayDeque<String>()
        val n = minOf(expectedTokens.size, actualTokens.size)
        for (i in 0 until n) {
            val e = expectedTokens[i]
            val a = actualTokens[i]
            val msg = compare(e, a, path)
            if (msg != null) return msg
        }
        if (expectedTokens.size != actualTokens.size) {
            return "${pathString(path)}: token count mismatch — expected ${expectedTokens.size}, got ${actualTokens.size}"
        }
        return null
    }

    private sealed class Token {
        class StartElement(val ns: String, val name: String, val attrs: Map<String, String>) : Token()
        class EndElement(val name: String) : Token()
        class Text(val content: String) : Token()
    }

    private fun tokenize(xml: String): List<Token> {
        val reader = xmlStreaming.newReader(xml)
        val out = mutableListOf<Token>()
        val pendingText = StringBuilder()
        try {
            while (reader.hasNext()) {
                val evt = reader.next()
                when (evt) {
                    EventType.TEXT,
                    EventType.CDSECT,
                    EventType.ENTITY_REF -> {
                        pendingText.append(reader.text)
                    }
                    EventType.IGNORABLE_WHITESPACE,
                    EventType.START_DOCUMENT,
                    EventType.END_DOCUMENT,
                    EventType.ATTRIBUTE,
                    EventType.PROCESSING_INSTRUCTION,
                    EventType.COMMENT,
                    EventType.DOCDECL -> {
                        // Skip — not part of the structural identity we
                        // diff against.
                    }
                    EventType.START_ELEMENT -> {
                        flushPendingText(pendingText, out)
                        out += Token.StartElement(
                            ns = reader.namespaceURI,
                            name = reader.localName,
                            attrs = collectAttrs(reader),
                        )
                    }
                    EventType.END_ELEMENT -> {
                        flushPendingText(pendingText, out)
                        out += Token.EndElement(reader.localName)
                    }
                }
            }
            flushPendingText(pendingText, out)
        } finally {
            reader.close()
        }
        return out
    }

    private fun flushPendingText(buf: StringBuilder, out: MutableList<Token>) {
        if (buf.isEmpty()) return
        val text = buf.toString()
        // Inter-element whitespace is dropped — only emit a TEXT token
        // when there's at least one non-whitespace character, matching
        // XMLUnit's ignoreWhitespace().
        if (text.any { !it.isWhitespace() }) {
            out += Token.Text(text)
        }
        buf.clear()
    }

    private fun compare(e: Token, a: Token, path: ArrayDeque<String>): String? = when {
        e is Token.StartElement && a is Token.StartElement -> {
            when {
                e.name != a.name ->
                    "${pathString(path)}: element name mismatch — expected <${e.name}>, got <${a.name}>"
                e.ns != a.ns ->
                    "${pathString(path)}<${e.name}>: namespace mismatch — expected '${e.ns}', got '${a.ns}'"
                e.attrs != a.attrs ->
                    attrMismatchMessage(e.attrs, a.attrs, "${pathString(path)}<${e.name}>")
                else -> {
                    path.addLast(e.name)
                    null
                }
            }
        }
        e is Token.EndElement && a is Token.EndElement -> {
            if (path.isNotEmpty()) path.removeLast()
            null
        }
        e is Token.Text && a is Token.Text -> {
            if (e.content == a.content) null
            else "${pathString(path)}: text mismatch — expected '${trunc(e.content)}', got '${trunc(a.content)}'"
        }
        else ->
            "${pathString(path)}: token-kind mismatch — expected ${kind(e)}, got ${kind(a)}"
    }

    private fun attrMismatchMessage(
        expected: Map<String, String>,
        actual: Map<String, String>,
        ctx: String,
    ): String {
        val onlyExpected = expected.keys - actual.keys
        val onlyActual = actual.keys - expected.keys
        val mismatched = expected.keys.intersect(actual.keys)
            .filter { expected[it] != actual[it] }
            .joinToString { "$it='${expected[it]}'≠'${actual[it]}'" }
        return buildString {
            append("$ctx: attribute mismatch")
            if (onlyExpected.isNotEmpty()) append(" — missing: $onlyExpected")
            if (onlyActual.isNotEmpty()) append(" — extra: $onlyActual")
            if (mismatched.isNotEmpty()) append(" — differing: $mismatched")
        }
    }

    private fun collectAttrs(reader: XmlReader): Map<String, String> {
        val out = HashMap<String, String>()
        for (i in 0 until reader.attributeCount) {
            val prefix = reader.getAttributePrefix(i)
            val local = reader.getAttributeLocalName(i)
            val name = if (prefix.isEmpty()) local else "$prefix:$local"
            if (name == "xmlns" || name.startsWith("xmlns:")) continue
            out[name] = reader.getAttributeValue(i)
        }
        return out
    }

    private fun kind(token: Token): String = when (token) {
        is Token.StartElement -> "<${token.name}>"
        is Token.EndElement -> "</${token.name}>"
        is Token.Text -> "text"
    }

    private fun pathString(path: ArrayDeque<String>): String =
        if (path.isEmpty()) "/" else "/" + path.joinToString("/")

    private fun trunc(s: String): String =
        if (s.length <= 60) s else s.substring(0, 60) + "…"
}
