package io.docxkt.util

import java.time.Instant

internal actual fun nowIso8601(): String = Instant.now().toString()
