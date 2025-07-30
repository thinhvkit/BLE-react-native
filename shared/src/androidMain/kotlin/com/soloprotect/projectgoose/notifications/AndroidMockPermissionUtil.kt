package com.myprotect.projectx.notifications

import android.content.Context
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.extensions.hasLocationPermission
import com.myprotect.projectx.extensions.hasNotificationPermission

/**
 * This class is only for checking notification permission,
 * for asking runtime permission use AndroidPermissionUtil in your activity.
 */
internal class AndroidMockPermissionUtil(private val context: Context) : PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        onPermissionResult(context.hasNotificationPermission())
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) = Unit.also {
        Logger.d(
            "In Android this function is just a mock. You need to ask permission in Activity " +
                    "using like below: \n" +
                    "val permissionUtil by permissionUtil()\n" +
                    "permissionUtil.askNotificationPermission() \n"
        )
    }

    override fun hasLocationPermission(onPermissionResult: (Boolean) -> Unit) {
        onPermissionResult(context.hasLocationPermission())
    }

    override fun askLocationPermission(onPermissionGranted: () -> Unit) {
        Logger.d(
            "In Android this function is just a mock. You need to ask permission in Activity " +
                    "using like below: \n" +
                    "val permissionUtil by permissionUtil()\n" +
                    "permissionUtil.askLocationPermission() \n"
        )
    }
}
