package com.myprotect.projectx.di

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.AppDataStoreManager
import com.myprotect.projectx.callCenter.AndroidCallCenterManager
import com.myprotect.projectx.callCenter.CallCenter
import com.myprotect.projectx.common.Sensors
import com.myprotect.projectx.firebase.FirebasePushNotifierImpl
import com.myprotect.projectx.haptic.AndroidHapticManager
import com.myprotect.projectx.haptic.IHaptic
import com.myprotect.projectx.incapacitation.AndroidIncapacitationManager
import com.myprotect.projectx.incapacitation.Timer
import com.myprotect.projectx.incapacitation.TimerManager
import com.myprotect.projectx.notifications.AndroidMockPermissionUtil
import com.myprotect.projectx.notifications.AndroidNotifier
import com.myprotect.projectx.notifications.NotificationChannelFactory
import com.myprotect.projectx.notifications.NotificationPlatformConfiguration
import com.myprotect.projectx.notifications.Notifier
import com.myprotect.projectx.notifications.PermissionUtil
import com.myprotect.projectx.notifications.PushNotifier
import com.myprotect.projectx.player.AndroidMediaPlayerController
import com.myprotect.projectx.player.MediaPlayerController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal lateinit var applicationContext: Context
    private set

internal class ContextInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        applicationContext = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}

internal actual fun platformModule(logEnable: Boolean) = module {
    factory { Platform.Android } bind Platform::class
    single { applicationContext }
    factory { AppDataStoreManager(applicationContext as Application) } bind AppDataStore::class
    factoryOf(::AndroidMockPermissionUtil) bind PermissionUtil::class
    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Android
        AndroidNotifier(
            context = get(),
            androidNotificationConfiguration = configuration,
            notificationChannelFactory = NotificationChannelFactory(
                context = get(), channelData = configuration.notificationChannelData
            ),
            permissionUtil = get()
        )
    } bind Notifier::class
    factoryOf(::FirebasePushNotifierImpl) bind PushNotifier::class
    factory {
        AndroidIncapacitationManager(
            context = get(),
            permissionUtil = get(),
            timerManager = TimerManager(),
            sensors = Sensors(get())
        )
    } bind Timer::class

    factory { AndroidMediaPlayerController(get()) } bind MediaPlayerController::class
    factory { AndroidHapticManager(get(), get()) } bind IHaptic::class
    factory { AndroidCallCenterManager(get(), get()) } bind CallCenter::class
}.also {
    if (logEnable){
        Napier.base(DebugAntilog())
    }
}
