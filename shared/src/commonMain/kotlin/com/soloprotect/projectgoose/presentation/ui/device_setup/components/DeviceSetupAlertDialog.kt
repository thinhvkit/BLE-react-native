package com.myprotect.projectx.presentation.ui.device_setup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.presentation.component.DefaultButton
import com.myprotect.projectx.presentation.component.Spacer_16dp
import com.myprotect.projectx.presentation.component.Spacer_8dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.theme.blue_01
import com.myprotect.projectx.presentation.theme.green_01
import com.myprotect.projectx.presentation.theme.red_01
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.bluetooth_button_custom
import myprotect_mobile.shared.generated.resources.bluetooth_grey
import myprotect_mobile.shared.generated.resources.check_circle
import myprotect_mobile.shared.generated.resources.red_alert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSetupAlertDialog(
    onDismissRequest: () -> Unit,
    status: SetupStatus
) {

    LaunchedEffect(status) {
        if (status == SetupStatus.SUCCESS) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                onDismissRequest()
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(blue_01).padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp).padding(4.dp),
                    painter = painterResource(Res.drawable.bluetooth_grey),
                    contentDescription = "",
                    tint = Color.White
                )
                Spacer_8dp()
                Text(
                    LocaleKeys.BT_COMPLETESETUP.tr(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamilies.futuraBold
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer_16dp()

            AnimatedVisibility(
                visible = status == SetupStatus.SUCCESS,
            ) {
                SetupSuccess()
            }

            AnimatedVisibility(
                visible = status == SetupStatus.FAILED,
            ) {
                SetupFailed(onDismissRequest)
            }

            AnimatedVisibility(
                visible = status == SetupStatus.PROCESSING,
            ) {
                SetupProcessing(onDismissRequest)
            }
        }
    }
}

@Composable
fun SetupProcessing(onDismissRequest: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
        Text(
            LocaleKeys.BT_SETUP_STEPS.tr(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer_16dp()

        Column(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
            Box(modifier = Modifier.padding(16.dp)) {
                Image(
                    modifier = Modifier.size(200.dp).align(Alignment.TopCenter),
                    painter = painterResource(Res.drawable.bluetooth_button_custom),
                    contentDescription = "",
                )
            }
        }

        Spacer_16dp()

        Row(
            modifier = Modifier.fillMaxWidth().background(green_01),
        ) {

            DefaultButton(
                modifier = Modifier.fillMaxWidth(),
                text = LocaleKeys.CANCEL.tr(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamilies.futuraBold
                ),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = green_01,
                    contentColor = Color.White
                ),
            ) {
                onDismissRequest()
            }
        }
    }
}

@Composable
fun SetupSuccess() {

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            LocaleKeys.BT_SETUPCOMPLETE.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontFamily = FontFamilies.futuraBold
            ),
            textAlign = TextAlign.Center,
            color = Color.Black,
        )

        Spacer_16dp()

        Box {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(Res.drawable.check_circle),
                contentDescription = "",
                tint = blue_01
            )
        }

        Spacer_16dp()
    }
}

@Composable
fun SetupFailed(onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            LocaleKeys.BT_SETUPFAILED.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontFamily = FontFamilies.futuraBold
            ),
            textAlign = TextAlign.Center,
            color = Color.Black,
        )

        Spacer_16dp()

        Text(
            LocaleKeys.BT_NO_RESPONSE.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontFamily = FontFamilies.futuraBold
            ),
            textAlign = TextAlign.Center,
            color = Color.Black,
        )

        Spacer_8dp()

        Icon(
            modifier = Modifier.size(48.dp).padding(4.dp),
            painter = painterResource(Res.drawable.red_alert),
            contentDescription = "",
            tint = red_01
        )

        Spacer_8dp()

        Text(
            LocaleKeys.BT_RETRY.tr(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontFamily = FontFamilies.futuraBold
            ),
            textAlign = TextAlign.Center,
            color = Color.Black,
        )

        Spacer_16dp()

        Row(
            modifier = Modifier.fillMaxWidth().background(green_01),
        ) {

            DefaultButton(
                modifier = Modifier.fillMaxWidth(),
                text = LocaleKeys.BUTTON_CLOSE.tr(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamilies.futuraBold
                ),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = green_01,
                    contentColor = Color.White
                ),
            ) {
                onDismissRequest()
            }
        }
    }
}

enum class SetupStatus {
    PROCESSING,
    SUCCESS,
    FAILED,
}
