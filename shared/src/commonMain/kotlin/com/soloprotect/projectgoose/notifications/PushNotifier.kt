package com.myprotect.projectx.notifications

typealias PayloadData = Map<String, *>

/**
 * Class represents push notification such as Firebase Push Notification
 */
interface PushNotifier {

    /**
     * @return current push notification token
     */
    suspend fun getToken(): String?

    /**
     * Deletes user push notification. For log out cases for example
     */
    suspend fun deleteMyToken()

    /**
     * Subscribing user to group.
     * @param topic  Topic name
     */
    suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribe user from group.
     * @param topic  Topic name
     */
    suspend fun unSubscribeFromTopic(topic: String)

}
