package com.myprotect.projectx.domain.core

sealed class UIMessage(val message: String) {

    class ErrorMessage(
        message: String
    ): UIMessage(message)

    class InfoMessage(
        message: String
    ): UIMessage(message)

}
