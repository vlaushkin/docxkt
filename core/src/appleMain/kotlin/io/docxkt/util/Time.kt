// No upstream analogue. Apple-side actual for the `nowIso8601` expect
// declared in commonMain/io/docxkt/util/Time.kt. JVM/Android side uses
// java.time.Instant.now().toString().
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.docxkt.util

import platform.Foundation.NSDate
import platform.Foundation.NSISO8601DateFormatWithFractionalSeconds
import platform.Foundation.NSISO8601DateFormatWithInternetDateTime
import platform.Foundation.NSISO8601DateFormatter

/**
 * Produces ISO-8601 in `YYYY-MM-DDTHH:mm:ss.sssZ` form (UTC, three
 * fractional digits). The JVM side emits `java.time.Instant.toString()`
 * which renders nanosecond precision when present; Apple Foundation
 * emits millisecond precision. Both shapes are valid ISO-8601 and Word
 * + LibreOffice accept either. Fixture tests pin the value explicitly,
 * so the per-platform precision delta is not load-bearing.
 */
internal actual fun nowIso8601(): String {
    val formatter = NSISO8601DateFormatter()
    formatter.formatOptions =
        NSISO8601DateFormatWithInternetDateTime or
            NSISO8601DateFormatWithFractionalSeconds
    return formatter.stringFromDate(NSDate())
}
