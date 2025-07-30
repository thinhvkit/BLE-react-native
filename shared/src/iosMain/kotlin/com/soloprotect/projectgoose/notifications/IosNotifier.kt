package com.myprotect.projectx.notifications

import com.myprotect.projectx.callCenter.CallCenterManager
import com.myprotect.projectx.common.Constants.CALL_CENTER
import com.myprotect.projectx.common.Constants.CANCEL_ACTION
import com.myprotect.projectx.common.Constants.EXTEND_ACTION
import com.myprotect.projectx.common.Constants.READY_TALK_ACTION
import com.myprotect.projectx.common.Constants.RED_ALERT_ACTION
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.common.UIDispatcher
import com.myprotect.projectx.extensions.onNotificationActionClicked
import com.myprotect.projectx.extensions.onNotificationClicked
import com.myprotect.projectx.extensions.onUserNotification
import com.myprotect.projectx.extensions.shouldShowNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationAction
import platform.UserNotifications.UNNotificationActionOptionForeground
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationCategoryOptionNone
import platform.UserNotifications.UNNotificationInterruptionLevel
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.ready2talk
import myprotect_mobile.shared.generated.resources.red_alert
import kotlin.random.Random

internal class IosNotifier(
    private val permissionUtil: IosPermissionUtil,
    private val notificationCenter: UNUserNotificationCenter,
) : Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        permissionUtil.askNotificationPermission {
            val notificationContent = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound)
                setUserInfo(userInfo + payloadData)
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                identifier = id.toString(),
                content = notificationContent,
                trigger = trigger
            )

            notificationCenter.addNotificationRequest(notificationRequest) { error ->
                error?.let { Logger.e("Error showing notification: $error") }
            }
        }
    }

    override fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String>,
        action: Boolean
    ) {
        permissionUtil.askNotificationPermission {
            val notificationContent = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound)
                setUserInfo(userInfo + payloadData)
                setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelPassive)
                setCategoryIdentifier(CALL_CENTER)
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
                identifier = id.toString(),
                content = notificationContent,
                trigger = trigger
            )

            val scope = CoroutineScope(UIDispatcher())

            scope.launch {
                // Define the custom actions.
                val redAlertAction = UNNotificationAction.actionWithIdentifier(
                    RED_ALERT_ACTION,
                    title = getString(Res.string.red_alert),
                    UNNotificationActionOptionForeground
                )
                val ready2TalkAction = UNNotificationAction.actionWithIdentifier(
                    READY_TALK_ACTION,
                    title = getString(Res.string.ready2talk),
                    UNNotificationActionOptionForeground
                )

                // Define the notification type
                val callCenterCategory =
                    UNNotificationCategory.categoryWithIdentifier(
                        identifier = CALL_CENTER,
                        actions = listOf(redAlertAction, ready2TalkAction),
                        intentIdentifiers = emptyList<Any>(),
                        hiddenPreviewsBodyPlaceholder = "",
                        options = UNNotificationCategoryOptionNone
                    )

                notificationCenter.setNotificationCategories(setOf(callCenterCategory))
                notificationCenter.addNotificationRequest(notificationRequest) { error ->
                    error?.let { Logger.e("Error showing notification: $error") }
                }
            }
        }
    }

    override fun notifyIncapacitation(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String>
    ) {

        // iOS will display a Live Activity instead of Local Notification .
        return
//        permissionUtil.askNotificationPermission {
//
//            return@askNotificationPermission
//
//            val notificationContent = UNMutableNotificationContent().apply {
//                setTitle(title)
//                setBody(body)
//                setSound(null)
//                setUserInfo(userInfo + payloadData)
//                setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelPassive)
//                setCategoryIdentifier(INCAPACITATION_CENTER)
//            }
//            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
//                1.0, false
//            )
//            val notificationRequest = UNNotificationRequest.requestWithIdentifier(
//                identifier = id.toString(),
//                content = notificationContent,
//                trigger = trigger
//            )
//
//            // Define the custom actions.
//            val extendAction = UNNotificationAction.actionWithIdentifier(
//                EXTEND_ACTION,
//                title = "Extend timer",
//                UNNotificationActionOptionForeground
//            )
//            val cancelAction = UNNotificationAction.actionWithIdentifier(
//                CANCEL_ACTION,
//                title = "Cancel timer",
//                UNNotificationActionOptionForeground
//            )
//
//            // Define the notification type
//            val callCenterCategory =
//                UNNotificationCategory.categoryWithIdentifier(
//                    identifier = INCAPACITATION_CENTER,
//                    actions = listOf(extendAction, cancelAction),
//                    intentIdentifiers = emptyList<Any>(),
//                    hiddenPreviewsBodyPlaceholder = "",
//                    options = UNNotificationCategoryOptionNone
//                )
//
//            notificationCenter.setNotificationCategories(setOf(callCenterCategory))
//            notificationCenter.addNotificationRequest(notificationRequest) { error ->
//                error?.let { println("Error showing notification: $error") }
//            }
//        }
    }


    override fun remove(id: Int) {
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(id.toString()))
    }

    override fun removeAll() {
        notificationCenter.removeAllDeliveredNotifications()
    }

    internal class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            didReceiveNotificationResponse: UNNotificationResponse,
            withCompletionHandler: () -> Unit,
        ) {

            val notificationContent =
                didReceiveNotificationResponse.notification.request.content

            NotifierManager.onUserNotification(notificationContent)

            val callCenter = CallCenterManager.getCallCenter()

            // Perform the task associated with the action.
            when (val action = didReceiveNotificationResponse.actionIdentifier) {
                RED_ALERT_ACTION -> {

                    callCenter.makeRedAlertCall()
                    NotifierManager.onNotificationClicked(notificationContent)
                }

                READY_TALK_ACTION -> {

                    callCenter.makeReady2TalkCall()
                    NotifierManager.onNotificationClicked(notificationContent)
                }

                EXTEND_ACTION, CANCEL_ACTION -> {
                    NotifierManager.onNotificationActionClicked(action)
                }

                // Handle other actions...
                else -> {}
            }

            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler()
        }

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            val notificationContent = willPresentNotification.request.content
            NotifierManager.onUserNotification(notificationContent)
            if (NotifierManager.shouldShowNotification(notificationContent)) withCompletionHandler(
                IosPermissionUtil.NOTIFICATION_PERMISSIONS
            )
        }
    }
}
