package com.myprotect.projectx.extensions

import android.content.Intent
import androidx.core.os.bundleOf
import com.myprotect.projectx.common.Constants.ACTION_NOTIFICATION_CLICK
import com.myprotect.projectx.common.Constants.KEY_ANDROID_FIREBASE_NOTIFICATION
import com.myprotect.projectx.notifications.NotificationPlatformConfiguration
import com.myprotect.projectx.notifications.NotifierManager
import com.myprotect.projectx.notifications.NotifierManagerImpl

fun NotifierManager.onCreateOrOnNewIntent(intent: Intent?) {
    if (intent == null) return
    val extras = intent.extras ?: bundleOf()
    val payloadData = mutableMapOf<String, Any>()

    val isNotificationClicked =
        extras.containsKey(ACTION_NOTIFICATION_CLICK)
                || extras.containsKey(KEY_ANDROID_FIREBASE_NOTIFICATION)
                || payloadData.containsKey(ACTION_NOTIFICATION_CLICK)

    extras.keySet().forEach { key ->
        val value = extras.get(key)
        value?.let { payloadData[key] = it }
    }


    if (extras.containsKey(KEY_ANDROID_FIREBASE_NOTIFICATION))
        NotifierManagerImpl.onPushPayloadData(payloadData.minus(ACTION_NOTIFICATION_CLICK))
    if (isNotificationClicked)
        NotifierManagerImpl.onNotificationClicked(payloadData.minus(ACTION_NOTIFICATION_CLICK))
}

internal fun NotifierManagerImpl.shouldShowNotification(): Boolean {
    val configuration =
        NotifierManagerImpl.getConfiguration() as? NotificationPlatformConfiguration.Android
    return configuration?.showPushNotification ?: true
}
