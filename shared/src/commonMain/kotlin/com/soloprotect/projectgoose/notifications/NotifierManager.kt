package com.myprotect.projectx.notifications

import androidx.compose.runtime.Composable

object NotifierManager {

    /**
     * Call initialize function on Application Start.
     * @param configuration pass either ios or android configuration depending on platform
     * @see NotificationPlatformConfiguration.Ios
     * @see NotificationPlatformConfiguration.Android
     */
    @Composable
    fun initialize(configuration: NotificationPlatformConfiguration) {
        NotifierManagerImpl.initialize(configuration)
    }

    /**
     * Creates local Notifier instance
     */
    fun getLocalNotifier(): Notifier {
        return NotifierManagerImpl.getLocalNotifier()
    }

    /**
     * Creates push Notifier instance (Firebase Push Notification)
     */
    fun getPushNotifier(): PushNotifier {
        return NotifierManagerImpl.getPushNotifier()
    }

    /**
     * For listening updates such as push notification token changes
     */
    fun addListener(listener: Listener) {
        NotifierManagerImpl.addListener(listener)
    }

    fun removeListener(listener: Listener) {
        NotifierManagerImpl.removeListener(listener)
    }

    /**
     *
     * Returns permission util that can be used to check and ask notification permission
     * However in Android you need to use in Activity like below:
     *
     * val permissionUtil by permissionUtil()
     * permissionUtil.askNotificationPermission() //this will ask permission in Android 13(API Level 33) or above, otherwise permission will be granted.
     *
     * @return PermissionUtil class instance
     */
    fun getPermissionUtil(): PermissionUtil {
        return NotifierManagerImpl.getPermissionUtil()
    }


    interface Listener {
        /**
         * Called when push notification token is updated, or initialized first time
         * @param token Push Notification token
         */
        fun onNewToken(token: String) {}

        /**
         * Called when "Push Notification" data type message is available
         * @see onPushNotification for receiving "Push Notification" notification type message.
         * @param data Push Notification Payload Data
         */
        fun onPayloadData(data: PayloadData) {}

        /**
         * Called when "Push Notification" notification type message is received.
         * @see onPayloadData for receiving "Push Notification" data type message.
         * @param title Notification title
         * @param body Notification body message
         */
        fun onPushNotification(title: String?, body: String?) {}

        /**
         * Called when notification is clicked
         * @param data Push Notification Payload Data
         */
        fun onNotificationClicked(data: PayloadData) {}

        /**
         * Called when notification is clicked
         * @param data Push Notification Payload Data
         */
        fun onNotificationActionClicked(action: String) {}
    }
}
