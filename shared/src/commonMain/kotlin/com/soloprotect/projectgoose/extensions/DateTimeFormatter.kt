package com.myprotect.projectx.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect fun LocalDateTime?.formatPattern(pattern: String): String


fun timestampToLocalDateTime(timestamp: Long?): LocalDateTime? {
    if (timestamp == null) return null

    // Convert the timestamp (milliseconds since epoch) to an Instant
    val instant = Instant.fromEpochMilliseconds(timestamp)

    // Convert the Instant to LocalDateTime using a specific time zone
    // For example, using the system's default time zone
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone)
}
