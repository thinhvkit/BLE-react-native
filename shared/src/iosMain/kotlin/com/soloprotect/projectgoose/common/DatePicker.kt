package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import com.myprotect.projectx.extensions.toNsDate
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle

class IOSDatePicker(private val minDate: NSDate? = null) : DatePicker {
    override fun show(onDateSelected: (day: Int, month: Int, year: Int) -> Unit) {
        val datePicker = UIDatePicker().apply {
            preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
            datePickerMode = UIDatePickerMode.UIDatePickerModeDate
            minDate?.let {
                minimumDate = it
            }
        }

        val alertController = UIAlertController.alertControllerWithTitle(
            title = "Select Date",
            message = "\n\n\n\n\n\n\n\n\n\n",
            preferredStyle = UIAlertControllerStyleActionSheet
        )

        alertController.view.addSubview(datePicker)

        val okAction = UIAlertAction.actionWithTitle(
            "OK",
            UIAlertActionStyleDefault
        ) { _ ->
            val calendar = NSCalendar.currentCalendar
            val components = calendar.components(
                NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
                datePicker.date
            )
            onDateSelected(components.day.toInt(), components.month.toInt(), components.year.toInt())
        }

        alertController.addAction(okAction)

        val cancelAction = UIAlertAction.actionWithTitle(
            "Cancel",
            UIAlertActionStyleCancel,
            null
        )
        alertController.addAction(cancelAction)

        val keyWindow = UIApplication.sharedApplication.keyWindow
        keyWindow?.rootViewController?.presentViewController(alertController, animated = true, completion = null)
    }
}

@Composable
actual fun provideDatePicker(minDate: LocalDateTime?): DatePicker = IOSDatePicker(minDate?.toNsDate())
