package com.myprotect.projectx.notifications

/**
 * Class that represent local notification
 */
interface Notifier {

    /**
     * Sends local notification to device
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information.
     * @return notification id
     */
    fun notify(
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    ): Int

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information
     */
    fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    )

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information
     * @param action Action item flag
     */
    fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap(),
        action: Boolean
    )

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information
     */
    fun notifyIncapacitation(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap(),
    )

    /**
     * Remove notification by id
     * @param id notification id
     */
    fun remove(id: Int)

    /**
     * Removes all previously shown notifications
     * @see remove(id) for removing specific notification.
     */
    fun removeAll()
}
