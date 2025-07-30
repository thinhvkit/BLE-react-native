package com.myprotect.projectx.notifications

/**
 * You can configure some customization for notifications depending on the platform
 */
sealed interface NotificationPlatformConfiguration {

    /**
     * Android Notification Customization. Create this object in android source.
     *
     * @param  notificationIconResId icon ResourceId (R.drawable.ic_notification)
     * @param notificationIconColorResId optional icon color ResourceId (R.color.yellow)
     * @param notificationChannelData optional notification channel data for General or Miscellaneous notifications
     * @see NotificationChannelData
     * @param showPushNotification Default value is true, by default when push notification is
     * received it will be shown to user. When set to false, it will not be shown to user,
     * but you can still get notification content using
     */
    class Android(
        val notificationIconResId: Int,
        val notificationIconColorResId: Int? = null,
        val notificationChannelData: NotificationChannelData = NotificationChannelData(),
        val showPushNotification: Boolean = true,
    ) : NotificationPlatformConfiguration {

        /**
         * By default Notification channel with below configuration is created but you can change it
         * @param id for General(or Miscellaneous or Other) notifications. Default value: "DEFAULT_NOTIFICATION_CHANNEL_ID"
         * @param name this is the title that is shown on app notification channels. Default value is "General"
         * Usually it is either General or Miscellaneous or Miscellaneous in most apps
         * @param description Notification description
         */
        class NotificationChannelData(
            val id: String = "DEFAULT_NOTIFICATION_CHANNEL_ID",
            val name: String = "General",
            val description: String = "",
        )

    }

    /**
     * Ios notification customization.
     *
     * @param showPushNotification Default value is true,
     * by default when push notification is received it will be shown to user.
     * When set to false, it will not be shown to user, but you can still get notification content using
     *
     * @param askNotificationPermissionOnStart Default value is true, when library is initialized it
     * will ask notification permission automatically from the user.
     * By setting askNotificationPermissionOnStart false, you can customize to ask permission whenever you want.
     */
    data class Ios(
        val showPushNotification: Boolean = true,
        val askNotificationPermissionOnStart: Boolean = true
    ) : NotificationPlatformConfiguration
}
