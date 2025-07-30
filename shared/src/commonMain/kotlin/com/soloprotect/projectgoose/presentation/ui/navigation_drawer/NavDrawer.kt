package com.myprotect.projectx.presentation.ui.navigation_drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.domain.models.risk_message.RiskMessage
import com.myprotect.projectx.common.BackPressHandler
import com.myprotect.projectx.common.ChangeStatusBarColors
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.notifications.NotifierManager
import com.myprotect.projectx.presentation.component.AppTopBar
import com.myprotect.projectx.presentation.component.Spacer_8dp
import com.myprotect.projectx.presentation.navigation.AppNavigation
import com.myprotect.projectx.presentation.navigation.MainDrawerNavigation
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.theme.blue_01
import com.myprotect.projectx.presentation.theme.green_01
import com.myprotect.projectx.presentation.theme.green_02
import com.myprotect.projectx.presentation.theme.red_01
import com.myprotect.projectx.presentation.ui.device_setup.DeviceSetupScreen
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupState
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupViewModel
import com.myprotect.projectx.presentation.ui.main.MainScreen
import com.myprotect.projectx.presentation.ui.main.view_model.MainViewModel
import com.myprotect.projectx.presentation.ui.navigation_drawer.view_model.NavigationDrawerEvent
import com.myprotect.projectx.presentation.ui.navigation_drawer.view_model.NavigationDrawerState
import com.myprotect.projectx.presentation.ui.risk_message.detail.RiskMessageDetailScreen
import com.myprotect.projectx.presentation.ui.risk_message.detail.view_model.RiskMessageDetailViewModel
import com.myprotect.projectx.presentation.ui.risk_message.list.RiskMessageScreen
import com.myprotect.projectx.presentation.ui.risk_message.list.view_model.RiskMessageViewModel
import com.myprotect.projectx.presentation.ui.setting.SettingScreen
import com.myprotect.projectx.presentation.ui.setting.view_model.SettingViewModel
import com.myprotect.projectx.presentation.ui.working_status.WorkingStatusDialog
import com.myprotect.projectx.presentation.ui.working_status.WorkingStatusDialogProvider
import com.myprotect.projectx.presentation.ui.working_status.WorkingStatusDialogState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.battery_full
import myprotect_mobile.shared.generated.resources.bell
import myprotect_mobile.shared.generated.resources.bluetooth_disconnected_white
import myprotect_mobile.shared.generated.resources.bluetooth_grey
import myprotect_mobile.shared.generated.resources.comment
import myprotect_mobile.shared.generated.resources.heartbeat
import myprotect_mobile.shared.generated.resources.keeping_you_safe
import myprotect_mobile.shared.generated.resources.settings
import myprotect_mobile.shared.generated.resources.sign_out

@Composable
fun NavDrawer(
    state: NavigationDrawerState,
    events: (NavigationDrawerEvent) -> Unit,
    deviceState: StateFlow<DeviceSetupState>,
    navigateToLogin: () -> Unit
) {
    ChangeStatusBarColors(MaterialTheme.colorScheme.surface)
    val navigationController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userName = state.profileName
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()

    LaunchedEffect(key1 = state.logout) {
        if (state.logout) {
            navigateToLogin()
        }
    }

    val workingStatusDialogState = remember { WorkingStatusDialogState() }

    val notifier = remember { NotifierManager.getLocalNotifier() }

    val deviceSetup by deviceState.collectAsState()

    val bleDevice = deviceSetup.devices[deviceSetup.bluDeviceConnected]?.peripheral

    val btActive = LocaleKeys.BT_ACTIVE.tr()
    val btUnavailable = LocaleKeys.BT_UNAVAILABLE.tr()
    val btBattery = LocaleKeys.BT_BATTERY.tr()

    LaunchedEffect(deviceSetup.bluDeviceConnected, bleDevice) {
        scope.launch {
            notifier.notify(
                id = 10,
                title = getString(Res.string.keeping_you_safe),
                body =
                if (deviceSetup.bluDeviceConnected != null) {
                    "Bluetooth Button: ${
                        if (bleDevice?.active == true) {
                            btActive + "\n$btBattery ${bleDevice.battery}%"
                        } else btUnavailable
                    }"
                } else "",
                payloadData = mapOf(Constants.RED_ALERT_ACTION to Constants.RED_ALERT_ACTION),
                action = true
            )
        }
    }

    BackPressHandler(
        onBackPressed = {
            workingStatusDialogState.showing.value = false
        },
        enable = workingStatusDialogState.showing.value
    )

    CompositionLocalProvider(
        WorkingStatusDialogProvider provides (workingStatusDialogState)
    ) {
        val localeManager = koinInject<LocaleManager>()
        localeManager.languageCode.value
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !workingStatusDialogState.showing.value,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    drawerShape = RectangleShape,
                    modifier = Modifier.fillMaxWidth(0.8f),
                ) {
                    Column(modifier = Modifier.fillMaxWidth().background(Color.Black)) {
                        Text(
                            "${LocaleKeys.GREETING.tr()}, $userName",
                            modifier = Modifier.padding(8.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White
                        )
                        HorizontalDivider(thickness = 3.dp, color = green_01)
                    }

                    NavDrawerItem(
                        section = LocaleKeys.ROLEDESCRIPTION_LONE_WORKER.tr(),
                        title = LocaleKeys.DASHBOARD_TITLE.tr(),
                        icon = painterResource(Res.drawable.bell),
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navigateToItem(MainDrawerNavigation.Main.route, navigationController)
                        })
                    NavDrawerItem(
                        section = LocaleKeys.WEBSITE_TITLE.tr(),
                        title = LocaleKeys.RISK_MESSAGING.tr(),
                        icon = painterResource(Res.drawable.comment),
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navigateToItem(
                                MainDrawerNavigation.RiskMessages.route,
                                navigationController
                            )
                        })
                    NavDrawerItem(
                        section = LocaleKeys.DEVICES_TITLE.tr(),
                        title = LocaleKeys.BT_DEVICESETUP.tr(),
                        icon = painterResource(Res.drawable.bluetooth_grey),
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navigateToItem(
                                MainDrawerNavigation.DeviceSetup.route,
                                navigationController
                            )
                        })

                    NavDrawerItem(
                        section = LocaleKeys.SETTINGS.tr(),
                        title = LocaleKeys.SETTINGS.tr(),
                        icon = painterResource(Res.drawable.settings),
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navigateToItem(
                                MainDrawerNavigation.Settings.route,
                                navigationController
                            )
                        })
                    NavDrawerItem(
                        section = null,
                        title = LocaleKeys.LOGOUT_LABEL.tr(),
                        icon = painterResource(Res.drawable.sign_out),
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                events(NavigationDrawerEvent.Logout)
                            }
                        })
                }
            },
        ) {
            Box {
                Scaffold(
                    topBar = {
                        val coroutineScope = rememberCoroutineScope()
                        Column {

                            AppTopBar(navigationIcon = {
                                when (navBackStackEntry?.destination?.route) {
                                    MainDrawerNavigation.Main.route, MainDrawerNavigation.Settings.route, MainDrawerNavigation.RiskMessages.route, MainDrawerNavigation.DeviceSetup.route -> {
                                        Icon(
                                            Icons.Rounded.Menu,
                                            contentDescription = "Menu",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp).clickable {
                                                coroutineScope.launch {
                                                    drawerState.open()
                                                }
                                            })
                                    }

                                    else -> Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp).clickable {
                                            navigationController.popBackStack()
                                        })
                                }
                            })

                            AppActionBar(
                                workingStatusDialogState,
                                bleActive = bleDevice?.active,
                                bleConnected = !deviceSetup.bluDeviceConnected.isNullOrEmpty(),
                                navController = navigationController
                            )
                        }
                    },
                ) {

                    val topPadding = it.calculateTopPadding()
                    NavHost(
                        navController = navigationController,
                        startDestination = MainDrawerNavigation.Main.route,
                        route = AppNavigation.MainNavigationHost.route,
                        modifier = Modifier.padding(0.dp, topPadding, 0.dp, 0.dp),
                    ) {
                        composable(route = MainDrawerNavigation.Main.route) {
                            val mainViewModel: MainViewModel = koinInject()
                            MainScreen(
                                mainState = mainViewModel.state.value,
                                mainEvents = mainViewModel::onTriggerEvent,
                            )
                        }
                        composable(route = MainDrawerNavigation.RiskMessages.route) {
                            val viewModel: RiskMessageViewModel = koinInject()

                            RiskMessageScreen(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateToDetail = { message ->
                                    val json = Json.encodeToString(
                                        value = message,
                                        serializer = RiskMessage.serializer()
                                    )
                                    navigationController.navigate(
                                        MainDrawerNavigation.RiskMessageDetail.route.replace(
                                            "{message}",
                                            json
                                        )
                                    )
                                },
                                navController = navigationController,
                            )
                        }

                        composable(route = MainDrawerNavigation.RiskMessageDetail.route) {
                            val viewModel: RiskMessageDetailViewModel = koinInject()
                            val json = navBackStackEntry?.arguments?.getString("message")
                            val message: RiskMessage? =
                                if (json != null) Json.decodeFromString<RiskMessage>(json) else null
                            RiskMessageDetailScreen(
                                message,
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                onSuccess = {
                                    navigationController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("needRefresh", true)
                                    navigationController.popBackStack()
                                },
                                navigateToDashboard = {
                                    navigateToItem(
                                        MainDrawerNavigation.Main.route,
                                        navigationController
                                    )
                                },
                                updateWorkingStatus = {
                                    workingStatusDialogState.showing.value = true
                                }
                            )
                        }

                        composable(route = MainDrawerNavigation.DeviceSetup.route) {
                            val viewModel: DeviceSetupViewModel = koinInject()
                            DeviceSetupScreen(
                                state = viewModel.deviceState,
                                events = viewModel::onTriggerEvent,
                            )
                        }

                        composable(route = MainDrawerNavigation.Settings.route) {
                            val viewModel: SettingViewModel = koinInject()
                            SettingScreen(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                            )
                        }
                    }
                }
                if (workingStatusDialogState.showing.value) {
                    WorkingStatusDialog(
                        onDismissRequest = {
                            workingStatusDialogState.showing.value = false
                        },
                    )
                }
            }
        }
    }
}

fun navigateToItem(route: String, navController: NavController) {
    navController.navigate(route) {
        popUpTo(navController.graph.route!!) {
            inclusive = true
        }
    }
}

@Composable
fun NavDrawerItem(section: String?, title: String, icon: Painter, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp)) {
        section?.let {
            Text(
                it, modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.Black

            )
        }
        HorizontalDivider(thickness = 1.dp, color = green_01)

        NavigationDrawerItem(
            label = {
                Text(
                    title,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    )
                )
            },
            selected = false,
            icon = {
                Box(modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = icon,
                        contentDescription = title,
                        tint = Color.Black
                    )
                }
            },
            onClick = onClick,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.LightGray,
                unselectedContainerColor = Color.White
            ),
        )
    }
}

@Composable
fun AppActionBar(
    workingStatusDialogState: WorkingStatusDialogState,
    bleActive: Boolean? = false,
    bleConnected: Boolean = false,
    navController: NavController
) {

    Card(
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column {

            Row(
                Modifier.fillMaxWidth().height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                            navigateToItem(
                                MainDrawerNavigation.Main.route,
                                navController
                            )
                    },
                    modifier = Modifier.weight(1f),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        disabledContentColor = Color.LightGray,
                        contentColor = green_01
                    )
                ) {
                    Icon(
                        painterResource(Res.drawable.bell),
                        modifier = Modifier.size(32.dp),
                        contentDescription = null,
                    )
                }
                Spacer_8dp()
                IconButton(
                    onClick = {
                        workingStatusDialogState.showing.value = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        disabledContentColor = green_02,
                        contentColor = Color.Gray,
                    )
                ) {
                    Icon(
                        painterResource(Res.drawable.heartbeat),
                        modifier = Modifier.size(32.dp),
                        contentDescription = null,
                    )
                }
            }

            if (bleConnected) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(if (bleActive == true) blue_01 else red_01)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp).padding(4.dp),
                        painter = if (bleActive == true) painterResource(Res.drawable.bluetooth_grey) else painterResource(
                            Res.drawable.bluetooth_disconnected_white
                        ),
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer_8dp()
                    Text(
                        "${LocaleKeys.BT_BTBUTTON.tr()} ${if (bleActive == true) LocaleKeys.BT_ACTIVE.tr() else LocaleKeys.BT_UNAVAILABLE.tr()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamilies.futuraBold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer_8dp()
                    Icon(
                        modifier = Modifier.size(32.dp).padding(4.dp),
                        painter = painterResource(Res.drawable.battery_full),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
