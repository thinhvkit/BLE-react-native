package com.myprotect.projectx.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.presentation.theme.blue_01
import com.myprotect.projectx.presentation.theme.red_01
import org.jetbrains.compose.resources.painterResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.ncap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncapacitationAlertDialog(
    onDismissRequest: () -> Unit,
) {

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = blue_01),
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ncap),
                    null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White,
                )
                Spacer_8dp()
                Text(
                    LocaleKeys.INCAPACITATION_ALERT.tr(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer_8dp()
                Text(
                    LocaleKeys.MONITORINGCENTREHASBEENCONTACTED.tr(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    LocaleKeys.ASSISTANCENOLONGERREQUIREDCANCELALERT.tr(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp).weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer_8dp()

                Box {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "00:00:00",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = red_01,
                    ),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp),
                        text = LocaleKeys.CANCELINCAP.tr(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
