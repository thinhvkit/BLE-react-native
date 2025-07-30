package com.myprotect.projectx.presentation.ui.device_setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprotect.projectx.ble.BLEPeripheralInterface
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.presentation.component.BallSwapIndicator
import com.myprotect.projectx.presentation.component.Spacer_16dp
import com.myprotect.projectx.presentation.component.Spacer_4dp
import com.myprotect.projectx.presentation.component.Spacer_8dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.ui.device_setup.components.DeviceSetupAlertDialog
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupEvent
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupState
import com.myprotect.projectx.presentation.ui.device_setup.view_model.EnhancedBluetoothPeripheral
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.bluetooth_grey

@Composable
fun DeviceSetupScreen(
    state: StateFlow<DeviceSetupState>,
    events: (DeviceSetupEvent) -> Unit,
) {

    val deviceState by state.collectAsState()

    val deviceSetup = deviceState.devices[deviceState.bluDeviceConnected]?.peripheral


    LaunchedEffect(true) {
        events(DeviceSetupEvent.Scan)
    }

    if (deviceState.setupDialog == UIComponentState.Show) {
        DeviceSetupAlertDialog(
            onDismissRequest = {
                events(DeviceSetupEvent.OnUpdateSetupDialog(UIComponentState.Hide))
            },
            status = deviceState.setupDialogStatus
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            if (deviceSetup != null) {
                DeviceDetail(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), deviceSetup
                )
            } else {
                val mac = deviceState.devices[deviceState.bluDeviceSelected]?.peripheral?.mac
                DeviceList(modifier = Modifier.fillMaxWidth().weight(1f)
                    .padding(horizontal = 12.dp),
                    devices = deviceState.devices,
                    deviceSelected = mac,
                    onDeviceSelected = { events(DeviceSetupEvent.OnSelectBluDevice(it)) })
            }

            Spacer_16dp()

        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (deviceState.progressBarState == ProgressBarState.ScreenLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    BallSwapIndicator()
                }
            }
            Spacer_4dp()
            Button(
                modifier = Modifier.width(200.dp).padding(8.dp),
                onClick = {
                    if (deviceSetup == null) {
                        deviceState.bluDeviceSelected?.let {
                            events(DeviceSetupEvent.OnUpdateSetupDialog(UIComponentState.Show))
                            events(DeviceSetupEvent.Connect(it))
                        }
                    } else {
                        events(DeviceSetupEvent.Disconnect(deviceSetup.mac))
                    }
                },
                shape = RoundedCornerShape(6.dp),
                enabled = deviceSetup != null || deviceState.bluDeviceSelected != null
            ) {
                Text(
                    if (deviceSetup == null) LocaleKeys.BT_CONTINUE.tr()
                    else LocaleKeys.BT_REMOVEDEVICE.tr(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamilies.futuraBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun DeviceList(
    modifier: Modifier = Modifier.fillMaxWidth(),
    devices: HashMap<String, EnhancedBluetoothPeripheral>,
    deviceSelected: String?,
    onDeviceSelected: (value: String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            LocaleKeys.BT_AVAILABLEDEVICES.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontSize = 24.sp,
                fontFamily = FontFamilies.futuraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer_16dp()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),

            ) {
            items(items = devices.values.toList(), key = { d ->
                d.peripheral.mac
            }) { d ->
                DeviceItem(modifier = Modifier.padding(2.dp).clickable {
                    onDeviceSelected(d.peripheral.mac)
                }, label = d.peripheral.run {
                    this.name + "-" + this.mac
                }, selected = d.peripheral.mac === deviceSelected
                )
            }
        }
    }
}

@Composable
fun DeviceItem(modifier: Modifier, label: String, selected: Boolean) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = if (selected) Color.LightGray else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Start) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.bluetooth_grey),
                contentDescription = null,
                tint = Color.Black
            )
            Spacer_8dp()
            Text(
                label, style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamilies.futuraBold,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
fun DeviceDetail(modifier: Modifier, device: BLEPeripheralInterface) {
    Column(modifier = modifier) {


        Spacer_16dp()

        Text(
            LocaleKeys.BT_DEVICEDETAILS_BTBUTTON.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontSize = 24.sp,
                fontFamily = FontFamilies.futuraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer_16dp()

        DeviceDetailItem("Device Name", device.name.let { it.ifEmpty { "" } })
        DeviceDetailItem(
            "Status", if (device.active) "Active" else "Unavailable"
        )
        DeviceDetailItem("Battery", "${device.battery}%")
        DeviceDetailItem("MAC Address", device.mac)
    }
}

@Composable
fun DeviceDetailItem(title: String, value: String) {
    Row(
        modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "$title:",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamilies.futuraBold, color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.End
        )

        Spacer_8dp()

        Text(
            value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ), textAlign = TextAlign.Start
        )
    }
}
