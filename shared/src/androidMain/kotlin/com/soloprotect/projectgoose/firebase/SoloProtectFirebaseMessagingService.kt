package com.myprotect.projectx.firebase;

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.extensions.shouldShowNotification
import com.myprotect.projectx.notifications.Notifier
import com.myprotect.projectx.notifications.NotifierManagerImpl

internal class myprotectFirebaseMessagingService : FirebaseMessagingService() {

    private val notifierManager by lazy { NotifierManagerImpl }
    private val notifier: Notifier by lazy { notifierManager.getLocalNotifier() }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("FirebaseMessaging: onNewToken is called")
        notifierManager.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val payloadData = message.data
        message.notification?.let {
            if (notifierManager.shouldShowNotification())
                notifier.notify(
                    title = it.title ?: "",
                    body = it.body ?: "",
                    payloadData = payloadData
                )

            notifierManager.onPushNotification(title = it.title, body = it.body)
        }
        if (payloadData.isNotEmpty()) {
            val data =
                payloadData + mapOf(Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
            notifierManager.onPushPayloadData(data)
        }
    }
}
