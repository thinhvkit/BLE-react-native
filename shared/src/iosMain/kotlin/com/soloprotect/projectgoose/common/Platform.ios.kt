package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import com.myprotect.projectx.notifications.NotificationPlatformConfiguration
import com.myprotect.projectx.notifications.NotifierManager
import platform.UIKit.UIScreen
import platform.darwin.NSObject
import platform.posix.exit

actual typealias Context = NSObject

@Composable
actual fun onApplicationStartPlatformSpecific() {
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = true
        )
    )
}

class IOSPlatform: PlatformInfo {
    override val name: PlatformSupported = PlatformSupported.IOS
}

actual fun getPlatform(): PlatformInfo = IOSPlatform()

actual fun closeApp() {
    exit(0)
}

@Composable
actual fun BackPressHandler(onBackPressed: () -> Unit, enable: Boolean) {}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight(): Int {
    val scale = UIScreen.mainScreen.scale
    return LocalWindowInfo.current.containerSize.height / scale.toInt()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidth(): Int {
    val scale = UIScreen.mainScreen.scale
    return LocalWindowInfo.current.containerSize.width / scale.toInt()
}
