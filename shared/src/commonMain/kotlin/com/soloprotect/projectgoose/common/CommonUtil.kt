package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class CommonUtil {

    companion object {

        private val EMAIL_PATTERN =
            Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")

        //        private val PHONE_PATTERN = Regex("[2-9][0-9]{7}\$")
        private val PHONE_PATTERN = Regex("^\\d{12}\$")
        private val OTP_PATTERN = Regex("^\\d{6}$")
        private val NUMBER_PATTERN = Regex("^\\d+\$")

        fun isValidEmail(email: String): Boolean {
            return email.matches(EMAIL_PATTERN)
        }

        fun checkOTPFormat(otp: String): Boolean {
            return otp.matches(OTP_PATTERN)
        }

        fun checkPhoneFormat(phone: String): Boolean {
            return phone.isNotEmpty() && phone.matches(PHONE_PATTERN)
        }

        fun isNumberOnly(phone: String): Boolean {
            return phone.isNotEmpty() && phone.matches(NUMBER_PATTERN)
        }
    }

}

expect fun randomUUID(): String

expect fun readJsonFile(fileName: String, fileType: String, context: Context): String

@Composable
expect fun ChangeStatusBarColors(statusBarColor: Color)
