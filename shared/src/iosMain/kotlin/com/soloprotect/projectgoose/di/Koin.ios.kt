package com.myprotect.projectx.di

import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.AppDataStoreManager
import com.myprotect.projectx.callCenter.CallCenter
import com.myprotect.projectx.callCenter.IosCallCenterManager
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.common.Sensors
import com.myprotect.projectx.firebase.FirebasePushNotifierImpl
import com.myprotect.projectx.haptic.IHaptic
import com.myprotect.projectx.haptic.IosHapticManager
import com.myprotect.projectx.incapacitation.IosIncapacitationManager
import com.myprotect.projectx.incapacitation.Timer
import com.myprotect.projectx.incapacitation.TimerManager
import com.myprotect.projectx.notifications.IosNotifier
import com.myprotect.projectx.notifications.IosPermissionUtil
import com.myprotect.projectx.notifications.Notifier
import com.myprotect.projectx.notifications.PermissionUtil
import com.myprotect.projectx.notifications.PushNotifier
import com.myprotect.projectx.player.IosMediaPlayerController
import com.myprotect.projectx.player.MediaPlayerController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.UserNotifications.UNUserNotificationCenter

internal actual fun platformModule(logEnable: Boolean) = module {
    factory { Platform.Ios } bind Platform::class
    single { AppDataStoreManager(Context()) } bind AppDataStore::class
    factory {
        IosPermissionUtil(
            notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        )
    } bind PermissionUtil::class
    factory {
        IosNotifier(
            permissionUtil = get(),
            notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        )
    } bind Notifier::class

    factory {
        FirebasePushNotifierImpl()
    } bind PushNotifier::class

    factory {
        IosIncapacitationManager(timerManager = TimerManager(), sensors = Sensors())
    } bind Timer::class

    factory {
        IosMediaPlayerController()
    } bind MediaPlayerController::class

    factory {
        IosHapticManager(get())
    } bind IHaptic::class

    factory {
        IosCallCenterManager(get())
    } bind CallCenter::class

}.also {
    if (logEnable) { Napier.base(DebugAntilog()) }
}
