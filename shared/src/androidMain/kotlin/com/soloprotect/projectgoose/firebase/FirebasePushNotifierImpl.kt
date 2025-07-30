package com.myprotect.projectx.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.notifications.PushNotifier
import kotlinx.coroutines.tasks.asDeferred

internal class FirebasePushNotifierImpl : PushNotifier {

    init {
        Logger.d("FirebasePushNotifier is initialized")
    }

    override suspend fun getToken(): String? {
        return FirebaseMessaging.getInstance().token.asDeferred().await()
    }

    override suspend fun deleteMyToken() {
        FirebaseMessaging.getInstance().deleteToken()
    }

    override suspend fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}
