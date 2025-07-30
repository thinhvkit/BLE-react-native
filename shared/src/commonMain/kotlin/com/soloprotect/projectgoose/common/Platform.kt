package com.myprotect.projectx.common

import androidx.compose.runtime.Composable

expect class Context

@Composable
expect fun onApplicationStartPlatformSpecific()

enum class PlatformSupported {
    ANDROID,
    IOS
}

interface PlatformInfo {
    val name: PlatformSupported
}

expect fun getPlatform(): PlatformInfo

expect fun closeApp()

@Composable
expect fun BackPressHandler(onBackPressed: () -> Unit, enable: Boolean = true)

@Composable
expect fun getScreenHeight(): Int

@Composable
expect fun getScreenWidth(): Int
