package com.myprotect.projectx.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.theme.green_01

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth().background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
        ) {

            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamilies.futuraBold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer_8dp()

            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
            )


            Spacer_32dp()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                DefaultButton(
                    modifier = Modifier.height(40.dp).padding(0.dp),
                    text = positiveButtonText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamilies.futuraBold
                    ),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Transparent,
                        contentColor = green_01
                    ),
                    border = BorderStroke(0.dp, Color.Transparent),
                    shape = MaterialTheme.shapes.small
                ) {
                    onPositiveClick()
                    onDismissRequest()
                }

                Spacer_2dp()

                DefaultButton(
                    modifier = Modifier.height(40.dp).padding(0.dp),
                    text = negativeButtonText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamilies.futuraBold
                    ),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Transparent,
                        contentColor = green_01
                    ),
                    border = BorderStroke(0.dp, Color.Transparent),
                    shape = MaterialTheme.shapes.small

                    ) {
                    onNegativeClick()
                    onDismissRequest()
                }
            }

        }

    }

}
