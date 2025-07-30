package com.myprotect.projectx.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.content.ContextCompat
import com.myprotect.projectx.R
import com.myprotect.projectx.common.Constants.ACTION_NOTIFICATION_CLICK
import com.myprotect.projectx.common.Constants.CANCEL_ACTION
import com.myprotect.projectx.common.Constants.EXTEND_ACTION
import com.myprotect.projectx.common.Constants.READY_TALK_ACTION
import com.myprotect.projectx.common.Constants.RED_ALERT_ACTION
import com.myprotect.projectx.extensions.notificationManager
import kotlin.random.Random

internal class AndroidNotifier(
    private val context: Context,
    private val androidNotificationConfiguration: NotificationPlatformConfiguration.Android,
    private val notificationChannelFactory: NotificationChannelFactory,
    private val permissionUtil: PermissionUtil,
) : Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }


    //https://developer.android.com/about/versions/14/behavior-changes-all#non-dismissable-notifications
    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }
        val notificationManager = context.notificationManager ?: return
        val pendingIntent = getPendingIntent(payloadData)

        notificationChannelFactory.createChannels()
        val notification = NotificationCompat.Builder(
            context,
            androidNotificationConfiguration.notificationChannelData.id
        ).apply {
            setChannelId(androidNotificationConfiguration.notificationChannelData.id)
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(androidNotificationConfiguration.notificationIconResId)
            setAutoCancel(true)
            setContentIntent(pendingIntent)

            androidNotificationConfiguration.notificationIconColorResId?.let {
                color = ContextCompat.getColor(context, it)
            }
        }.build()

        notificationManager.notify(id, notification)
    }

    override fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String>,
        action: Boolean
    ) {
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }

        val notificationManager = context.notificationManager ?: return
        val notification = createRedAlertNotification(title, body, payloadData)
        notificationManager.notify(id, notification)
    }

    fun createRedAlertNotification(
        title: String,
        body: String,
        payloadData: Map<String, String>
    ): Notification {
        notificationChannelFactory.createChannels()
        val pendingIntent = getPendingIntent(payloadData)
        val notification = NotificationCompat.Builder(
            context,
            androidNotificationConfiguration.notificationChannelData.id
        ).apply {
            setChannelId(androidNotificationConfiguration.notificationChannelData.id)
            setContentTitle(title)
            setContentText(body)
            setLargeIcon(Icon.createWithResource(context, R.drawable.app_logo))
            setSmallIcon(androidNotificationConfiguration.notificationIconResId)
            setSilent(true)
            setOngoing(true)
            setAutoCancel(false)
            setContentIntent(pendingIntent)

            addAction(
                Action(
                    R.drawable.app_logo,
                    context.getString(R.string.red_alert),
                    getBroadcastIntent(payloadData, RED_ALERT_ACTION)
                )
            )
            addAction(
                Action(
                    R.drawable.headset,
                    context.getString(R.string.ready2talk),
                    getBroadcastIntent(payloadData, READY_TALK_ACTION)
                )
            )

            androidNotificationConfiguration.notificationIconColorResId?.let {
                color = ContextCompat.getColor(context, it)
            }
        }.build()

        return notification
    }

    private fun createIncapacitationNotification(
        title: String,
        body: String,
        payloadData: Map<String, String>
    ): Notification {
        notificationChannelFactory.createChannels()
        val notification = NotificationCompat.Builder(
            context,
            androidNotificationConfiguration.notificationChannelData.id
        ).apply {
            setChannelId(androidNotificationConfiguration.notificationChannelData.id)
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.app_logo)
            setSilent(true)
            setLargeIcon(Icon.createWithResource(context, R.drawable.incap))
            addAction(
                Action(
                    R.drawable.plus,
                    "Extend timer",
                    getBroadcastIntent(payloadData, EXTEND_ACTION)
                )
            )
            addAction(
                Action(
                    R.drawable.cross,
                    "Cancel timer",
                    getBroadcastIntent(payloadData, CANCEL_ACTION)
                )
            )

            androidNotificationConfiguration.notificationIconColorResId?.let {
                color = ContextCompat.getColor(context, it)
            }
        }.build()

        return notification
    }

    override fun notifyIncapacitation(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String>,
    ) {
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }
        val notificationManager = context.notificationManager ?: return
        notificationManager.notify(id, createIncapacitationNotification(title, body, payloadData))
    }

    override fun remove(id: Int) {
        val notificationManager = context.notificationManager ?: return
        notificationManager.cancel(id)
    }

    override fun removeAll() {
        val notificationManager = context.notificationManager ?: return
        notificationManager.cancelAll()
    }

    private fun getPendingIntent(payloadData: Map<String, String>): PendingIntent? {
        val intent = getLauncherActivityIntent()?.apply {
            putExtra(ACTION_NOTIFICATION_CLICK, ACTION_NOTIFICATION_CLICK)
            payloadData.forEach { putExtra(it.key, it.value) }
        }
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    private fun getBroadcastIntent(
        payloadData: Map<String, String>,
        action: String
    ): PendingIntent? {
        val intent = Intent(action).apply {
            putExtra(action, action)
            payloadData.forEach { putExtra(it.key, it.value) }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    private fun getLauncherActivityIntent(): Intent? {
        val packageManager = context.applicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
    }

}
