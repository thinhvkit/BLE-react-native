package com.myprotect.projectx.common

import android.util.Log

actual object LoggerNative {
    actual fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }
}
