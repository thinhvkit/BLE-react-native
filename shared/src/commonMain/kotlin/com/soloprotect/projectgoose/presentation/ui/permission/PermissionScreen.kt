package com.myprotect.projectx.presentation.ui.permission

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.extensions.safe
import com.myprotect.projectx.permissions.PermissionCallback
import com.myprotect.projectx.permissions.PermissionStatus
import com.myprotect.projectx.permissions.PermissionType
import com.myprotect.projectx.permissions.createPermissionsManager
import com.myprotect.projectx.permissions.permissionRequests
import com.myprotect.projectx.presentation.component.GeneralAlertDialog
import com.myprotect.projectx.presentation.component.PermissionItem
import com.myprotect.projectx.presentation.component.Spacer_8dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.theme.Grey
import com.myprotect.projectx.presentation.theme.GreyLight
import com.myprotect.projectx.presentation.theme.SecondaryColor
import com.myprotect.projectx.presentation.theme.green_01
import com.myprotect.projectx.presentation.theme.splashBackground
import com.myprotect.projectx.presentation.ui.permission.view_model.PermissionEvent
import com.myprotect.projectx.presentation.ui.permission.view_model.PermissionState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.cancel
import myprotect_mobile.shared.generated.resources.cancel_dialog_permissions
import myprotect_mobile.shared.generated.resources.cancel_dialog_title
import myprotect_mobile.shared.generated.resources.info_circle
import myprotect_mobile.shared.generated.resources.next
import myprotect_mobile.shared.generated.resources.permission_sub_title
import myprotect_mobile.shared.generated.resources.permission_title
import myprotect_mobile.shared.generated.resources.shield_check

@Composable
fun PermissionScreen(
    state: PermissionState,
    events: (PermissionEvent) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit,
    exit: () -> Unit,
) {

    var launchPermission by remember { mutableStateOf(value = false) }
    var permissionType by remember { mutableStateOf(value = PermissionType.PHONE) }
    var launchSetting by remember { mutableStateOf(value = false) }


    LaunchedEffect(key1 = state.navigateToMain) {
        if (state.navigateToMain) {
            navigateToMain()
        }
    }

    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    launchPermission = false
                }

                else -> {
                    events(PermissionEvent.OnUpdatePermissionDialog(UIComponentState.Show))
                }
            }
        }
    })

    val permissions = permissionRequests()
    val permissionCheckedStates = remember {
        mutableStateMapOf(
            *permissions.map { it to false }.toTypedArray()
        )
    }

    for (p in permissions) {
        permissionCheckedStates[p] = permissionsManager.isPermissionGranted(p)
    }

    if (launchPermission) {
        if (permissionsManager.isPermissionGranted(permissionType)) {
            permissionCheckedStates[permissionType] = true
        } else {
            permissionsManager.askPermission(permissionType)
        }
        launchPermission = false
    }

    if (launchSetting) {
        permissionsManager.LaunchSettings()
        launchSetting = false
    }

    if (permissionType == PermissionType.LOCATION && permissionCheckedStates[permissionType].safe()) {
        permissionsManager.LaunchLocationSettingsIfNeed()
    }
    if (state.permissionDialog == UIComponentState.Show) {
        GeneralAlertDialog(title = "Permission Required",
            message = "Please grant this permission. You can manage permissions in your device settings.",
            positiveButtonText = "Settings",
            negativeButtonText = "Cancel",
            onDismissRequest = {
                events(PermissionEvent.OnUpdatePermissionDialog(UIComponentState.Hide))
            },
            onPositiveClick = {
                launchSetting = true
            },
            onNegativeClick = {
            })

    }

    if (state.cancelDialog == UIComponentState.Show) {
        GeneralAlertDialog(title = stringResource(Res.string.cancel_dialog_title),
            message = stringResource(Res.string.cancel_dialog_permissions),
            positiveButtonText = "CLOSE",
            negativeButtonText = "BACK",
            onDismissRequest = {
                events(PermissionEvent.OnUpdateCancelDialog(UIComponentState.Hide))
            },
            onPositiveClick = {
                exit()
            },
            onNegativeClick = {
            })

    }

    fun onChanged(value: Boolean, type: PermissionType) {
        permissionType = type
        launchPermission = value
    }

    Box(
        modifier = Modifier.fillMaxSize().background(splashBackground).padding(18.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {

        Card(
            modifier = Modifier.padding(2.dp),
            border = BorderStroke(1.dp, green_01)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.shield_check),
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = SecondaryColor
                    )
                    Spacer_8dp()
                    Text(
                        stringResource(Res.string.permission_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamilies.futuraBold,
                            color = Color.Black,
                        )
                    )
                }

                Spacer_8dp()

                Column(
                    modifier = Modifier.fillMaxHeight().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    Card(
                        modifier = Modifier.padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = GreyLight),
                        shape = CardDefaults.elevatedShape,
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.info_circle),
                                null,
                                modifier = Modifier.size(24.dp),
                                tint = Grey
                            )
                            Spacer_8dp()
                            Text(
                                stringResource(Res.string.permission_sub_title),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    //Permissions
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        permissionCheckedStates.entries.sortedBy {
                            it.key
                        }.forEachIndexed { index, entry ->
                            val type = entry.key
                            PermissionItem(
                                label = stringResource(type.label),
                                desc = stringResource(type.description),
                                selected = permissionsManager.isPermissionGranted(type),
                                icon = painterResource(type.icon),
                                onChanged = { onChanged(it, type) })

                            if (index < permissionCheckedStates.entries.size - 1) {
                                HorizontalDivider(color = green_01, thickness = 2.dp)
                            }
                        }
                    }

                    //Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            modifier = Modifier.size(140.dp, 40.dp),
                            onClick = {
                                events(PermissionEvent.OnUpdateCancelDialog(UIComponentState.Show))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = green_01),
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                text = stringResource(Res.string.cancel),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamilies.futuraBold
                                )
                            )
                        }
                        Button(
                            modifier = Modifier.size(140.dp, 40.dp),
                            onClick = {
                                navigateToLogin()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = green_01,
                                disabledContainerColor = GreyLight,
                                disabledContentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.extraSmall,
                            enabled = permissionCheckedStates.all { it.value }
                        ) {
                            Text(
                                text = stringResource(Res.string.next),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamilies.futuraBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
