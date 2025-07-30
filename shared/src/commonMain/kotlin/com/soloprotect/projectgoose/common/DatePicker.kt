package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDateTime

interface DatePicker {
    fun show(onDateSelected: (day: Int, month: Int, year: Int) -> Unit)
}

@Composable
expect fun provideDatePicker(minDate: LocalDateTime? = null): DatePicker
