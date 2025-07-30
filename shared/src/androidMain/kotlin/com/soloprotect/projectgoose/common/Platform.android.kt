package com.myprotect.projectx.common

import android.app.Application
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.myprotect.projectx.R
import com.myprotect.projectx.notifications.NotificationPlatformConfiguration
import com.myprotect.projectx.notifications.NotifierManager
import kotlin.system.exitProcess

actual typealias Context = Application

@Composable
actual fun onApplicationStartPlatformSpecific() {
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            notificationIconResId = R.drawable.app_logo,
            showPushNotification = true,
        )
    )
}

class AndroidPlatform : PlatformInfo {
    override val name: PlatformSupported = PlatformSupported.ANDROID
}

actual fun getPlatform(): PlatformInfo = AndroidPlatform()
actual fun closeApp() {
    exitProcess(0)
}

@Composable
actual fun BackPressHandler(onBackPressed: () -> Unit, enable: Boolean) {
    BackHandler(onBack = onBackPressed, enabled = enable)
}

@Composable
actual fun getScreenHeight(): Int {
    return getScreenHeight(LocalContext.current) // You'll need to pass or otherwise access the context
}

@Composable
actual fun getScreenWidth(): Int {
    return getScreenWidth(LocalContext.current) // You'll need to pass or otherwise access the context
}

fun getScreenHeight(context: android.content.Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val displayMetrics = context.resources.displayMetrics
        windowMetrics.bounds.height() / displayMetrics.density.toInt()
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels / displayMetrics.density.toInt()
    }
}

fun getScreenWidth(context: android.content.Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val displayMetrics = context.resources.displayMetrics
        windowMetrics.bounds.width() / displayMetrics.density.toInt()
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels / displayMetrics.density.toInt()
    }
}
