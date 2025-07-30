package com.myprotect.projectx.common

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.myprotect.projectx.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

class AndroidDatePicker(private val context: Context, private val minDate: Long?): DatePicker {

    override fun show(onDateSelected: (day: Int, month: Int, year: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.DialogTheme,
            { _, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                onDateSelected(selectedDay, selectedMonth + 1, selectedYear)
            }, year, month, day
        )
        minDate?.let {
            datePickerDialog.datePicker.minDate = it
        }
        datePickerDialog.show()
    }
}

@Composable
actual fun provideDatePicker(minDate: LocalDateTime?): DatePicker {
    val context = LocalContext.current
    val minDateInMillis = minDate?.toJavaLocalDateTime()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    return AndroidDatePicker(context = context, minDate = minDateInMillis)
}

