package com.myprotect.projectx.common

import platform.Foundation.NSLog

actual object LoggerNative {
    actual fun debug(tag : String, message: String) {
        NSLog("%s: %s", tag, message)
    }
}
