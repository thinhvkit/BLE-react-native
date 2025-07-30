package com.myprotect.projectx.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

@OptIn(ExperimentalForeignApi::class)
fun LocalDateTime.toNsDate(): NSDate? {
    val calendar = NSCalendar.currentCalendar
    val components = NSDateComponents()
    components.year = this.year.convert()
    components.month = this.monthNumber.convert()
    components.day = this.dayOfMonth.convert()
    components.hour = this.hour.convert()
    components.minute = this.minute.convert()
    components.second = this.second.convert()
    return calendar.dateFromComponents(components)
}

actual fun LocalDateTime?.formatPattern(pattern: String): String {
    this ?: return ""
    val date = this.toNsDate() ?: return ""
    val formatter = NSDateFormatter().apply {
        dateFormat = pattern
        locale = NSLocale.currentLocale
    }
    return formatter.stringFromDate(date)
}

fun NSDate.toKotlinLocalDateTime(): LocalDateTime {
    val calendar = NSCalendar.currentCalendar
    return LocalDateTime(
        year = calendar.component(NSCalendarUnitYear, this).toInt(),
        monthNumber = calendar.component(NSCalendarUnitMonth, this).toInt(),
        dayOfMonth = calendar.component(NSCalendarUnitDay, this).toInt(),
        hour = calendar.component(NSCalendarUnitHour, this).toInt(),
        minute = calendar.component(NSCalendarUnitMinute, this).toInt(),
        second = calendar.component(NSCalendarUnitSecond, this).toInt()
    )
}
