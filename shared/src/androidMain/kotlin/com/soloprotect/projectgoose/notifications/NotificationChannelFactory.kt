package com.myprotect.projectx.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.myprotect.projectx.extensions.notificationManager

internal class NotificationChannelFactory(
    private val context: Context,
    private val channelData: NotificationPlatformConfiguration.Android.NotificationChannelData,
) {

    fun createChannels() {
        val notificationManager = context.notificationManager ?: return

        val channel = NotificationChannel(
            channelData.id,
            channelData.name,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            this.description = channelData.description
            enableLights(true)
        }

        notificationManager.createNotificationChannel(channel)

    }

}
