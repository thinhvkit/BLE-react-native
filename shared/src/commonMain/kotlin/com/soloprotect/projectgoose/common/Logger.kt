package com.myprotect.projectx.common

import com.myprotect.projectx.common.Constants.LOGGER_TAG
import io.github.aakira.napier.Napier

expect object LoggerNative {
    fun debug(tag: String, message: String)
}

object Logger {
    fun d(message: String, tag: String = LOGGER_TAG) {
        Napier.d(message, tag = tag)
    }

    fun w(message: String, tag: String = LOGGER_TAG) {
        Napier.w(message, tag = tag)
    }

    fun i(message: String, tag: String = LOGGER_TAG) {
        Napier.i(message, tag = tag)
    }

    fun e(message: String, tag: String = LOGGER_TAG) {
        Napier.e(message, tag = tag)
    }
}

