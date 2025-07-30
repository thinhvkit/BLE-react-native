package com.myprotect.projectx.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun LocalDateTime?.formatPattern(pattern: String): String {
    this ?: return ""
    val str = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        .format(this.toJavaLocalDateTime())
    return str
}

