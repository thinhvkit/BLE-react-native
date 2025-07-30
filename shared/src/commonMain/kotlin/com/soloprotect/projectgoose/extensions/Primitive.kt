package com.myprotect.projectx.extensions

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

fun Boolean.negative() = !this

fun Boolean?.safe(default: Boolean = false) = this ?: default

fun String?.safe(default: String = ""): String = if (this.isNullOrEmpty()) default else this

fun Int?.safe(default: Int = 0): Int = this ?: default

fun Float?.safe(default: Float = 0f): Float = this ?: default

fun Double?.safe(default: Double = 0.0): Double = this ?: default

fun Long.toBoolean(): Boolean = this != 0L

fun Boolean.toLong(): Long = if (this) {
    1L
} else {
    0L
}

fun Long.formatTime(): String {
    val h: Long = this.milliseconds.inWholeHours
    val m: Long = this.milliseconds.inWholeMinutes - h.hours.inWholeMinutes
    val s: Long =
        this.milliseconds.inWholeSeconds - m.minutes.inWholeSeconds - h.hours.inWholeSeconds

    val hh: String = if (h in 0..9) "0$h" else "$h"
    val mm: String = if (m in 0..9) "0$m" else "$m"
    val ss: String = if (s in 0..9) "0$s" else "$s"

    return "${hh}:${mm}:${ss}"
}

fun Long.convertTime(): Triple<Long, Long, Long> {
    if (this <= 0L) return Triple(0L, 0L, 0L)
    val h: Long = this.milliseconds.inWholeHours
    val m: Long = this.milliseconds.inWholeMinutes - h.hours.inWholeMinutes
    val s: Long =
        this.milliseconds.inWholeSeconds - m.minutes.inWholeSeconds - h.hours.inWholeSeconds
    return Triple(h, m, s)
}

fun String.replaceWithArgs(args: List<String>) = Regex("""%(\d)\$[ds]""").replace(this) { matchResult ->
    args[matchResult.groupValues[1].toInt() - 1]
}
