package com.myprotect.projectx.di

import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.notifications.NotificationPlatformConfiguration
import com.myprotect.projectx.notifications.PermissionUtil
import com.myprotect.projectx.notifications.PushNotifier
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module


internal object LibDependencyInitializer {
    var koinApp: KoinApplication? = null
        private set

    fun initialize(configuration: NotificationPlatformConfiguration) {
        if (isInitialized()) return
        val configModule = module {
            single { configuration }
        }
        koinApp = koinApplication {
            modules(configModule + platformModule())
        }.also {
            it.koin.onLibraryInitialized()
        }
    }

    fun isInitialized() = koinApp != null
}

private fun Koin.onLibraryInitialized() {
    Logger.d("Library is initialized")
    val permissionUtil by inject<PermissionUtil>()
    val platform by inject<Platform>()
    val configuration by inject<NotificationPlatformConfiguration>()

    get<PushNotifier>() //This will make sure that that when lib is initialized, init method is called

    when (platform) {
        Platform.Android -> Unit //In Android platform permission should be asked in activity
        Platform.Ios -> {
            val askNotificationPermissionOnStart =
                (configuration as? NotificationPlatformConfiguration.Ios)?.askNotificationPermissionOnStart
                    ?: true
            if (askNotificationPermissionOnStart) permissionUtil.askNotificationPermission()
        }
    }
}
