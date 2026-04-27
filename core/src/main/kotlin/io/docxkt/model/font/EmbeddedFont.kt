// Port of: src/file/fonts/font-wrapper.ts (FontWrapper + obfuscate-ttf-to-odttf.ts).
package io.docxkt.model.font

/**
 * A user-supplied font family for embedding into the document.
 *
 * `Document` always treats fonts as REGULAR-only (matches upstream's
 * `createRegularFont` helper). Bold / italic / bold-italic embeds
 * exist in OOXML but upstream's public DSL surface only emits regular,
 * so we mirror.
 *
 * The 32-character GUID-shaped [fontKey] is used as the OOXML
 * obfuscation key — see [obfuscateTrueType]. Pass-through: we don't
 * validate that [bytes] is a valid TTF.
 */
internal data class EmbeddedFont(
    val name: String,
    val bytes: ByteArray,
    val characterSet: FontCharacterSet = FontCharacterSet.ANSI,
) {
    /**
     * Deterministic obfuscation key. We use a fixed sentinel
     * (`"00000000-0000-0000-0000-000000000000"`) for byte-stable
     * output across runs — upstream calls `uniqueUuid()` which
     * produces a fresh random GUID each render. The [obfuscateTrueType]
     * algorithm is fully determined by `(bytes, fontKey)`, so a
     * fixed key gives reproducible binary parts.
     */
    internal val fontKey: String = SENTINEL_FONT_KEY

    internal val obfuscatedBytes: ByteArray by lazy { obfuscateTrueType(bytes, fontKey) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmbeddedFont) return false
        return name == other.name &&
            bytes.contentEquals(other.bytes) &&
            characterSet == other.characterSet
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + characterSet.hashCode()
        return result
    }

    internal companion object {
        internal const val SENTINEL_FONT_KEY: String =
            "00000000-0000-0000-0000-000000000000"
    }
}

/**
 * Character-set tokens for `<w:charset w:val="…"/>`. Wire form is the
 * 2-character upper-hex code. Mirrors upstream's `CharacterSet` const
 * object in `src/file/fonts/font.ts`.
 */
public enum class FontCharacterSet(internal val wire: String) {
    ANSI("00"),
    DEFAULT("01"),
    SYMBOL("02"),
    MAC("4D"),
    JIS("80"),
    HANGUL("81"),
    JOHAB("82"),
    GB_2312("86"),
    CHINESEBIG5("88"),
    GREEK("A1"),
    TURKISH("A2"),
    VIETNAMESE("A3"),
    HEBREW("B1"),
    ARABIC("B2"),
    BALTIC("BA"),
    RUSSIAN("CC"),
    THAI("DE"),
    EASTEUROPE("EE"),
    OEM("FF"),
}

/**
 * OOXML font obfuscation per ECMA-376 Part 2 §11.1.
 *
 * XOR's the first 32 bytes of [bytes] with the [fontKey] GUID's hex
 * digits taken in REVERSED byte order. Bytes 32+ are passed through
 * unchanged.
 *
 * @param fontKey 32-hex-character GUID with or without dashes.
 */
internal fun obfuscateTrueType(bytes: ByteArray, fontKey: String): ByteArray {
    val guid = fontKey.replace("-", "")
    require(guid.length == 32) { "fontKey GUID must be 32 hex characters: $fontKey" }
    val hexBytes = ByteArray(16) { i ->
        // Take pairs of hex characters and parse as unsigned byte.
        guid.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    hexBytes.reverse()

    val end = minOf(32, bytes.size)
    val out = bytes.copyOf()
    for (i in 0 until end) {
        out[i] = (out[i].toInt() xor hexBytes[i % hexBytes.size].toInt()).toByte()
    }
    return out
}
