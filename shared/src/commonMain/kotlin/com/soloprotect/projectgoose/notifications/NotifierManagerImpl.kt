package com.myprotect.projectx.notifications

import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.di.KMPKoinComponent
import com.myprotect.projectx.di.LibDependencyInitializer
import org.koin.core.component.get

internal object NotifierManagerImpl : KMPKoinComponent() {
    private val listeners = mutableListOf<NotifierManager.Listener>()

    fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    fun getConfiguration(): NotificationPlatformConfiguration = get()

    fun getLocalNotifier(): Notifier {
        requireInitialization()
        return get()
    }

    fun getPushNotifier(): PushNotifier {
        requireInitialization()
        return get()
    }

    fun getPermissionUtil(): PermissionUtil {
        requireInitialization()
        return get()
    }

    fun addListener(listener: NotifierManager.Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: NotifierManager.Listener) {
        listeners.remove(listener)
    }

    fun onNewToken(token: String) {
        listeners.forEach { it.onNewToken(token) }
    }

    fun onPushPayloadData(data: PayloadData) {
        Logger.d("Received Push Notification payload data")
        if (listeners.size == 0) Logger.d("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onPayloadData(data) }
    }

    fun onPushNotification(title: String?, body: String?) {
        Logger.d("Received Push Notification notification type message")
        if (listeners.size == 0) Logger.d("There is no listener to notify onPushNotification")
        listeners.forEach { it.onPushNotification(title = title, body = body) }
    }

    fun onNotificationClicked(data: PayloadData) {
        Logger.d("Notification is clicked, $data")
        if (listeners.size == 0) Logger.d("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onNotificationClicked(data) }
    }

    fun onNotificationActionClicked(action: String){
        Logger.d("Notification Action is clicked, $action")
        if (listeners.size == 0) Logger.d("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onNotificationActionClicked(action) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

}
