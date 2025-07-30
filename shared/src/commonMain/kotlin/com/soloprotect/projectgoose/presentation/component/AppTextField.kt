package com.myprotect.projectx.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprotect.projectx.presentation.theme.FontFamilies

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    hint: String = "",
    enable: Boolean = true,
    height: Dp = 45.dp,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    textStyle: TextStyle? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Column(horizontalAlignment = Alignment.Start) {
        if (label != null) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamilies.openSans,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Start,
            )
            Spacer_4dp()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.TopStart
        ) {
            BasicTextField(
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                enabled = enable,
                modifier = modifier.fillMaxWidth().background(color = Color.White).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ).clip(shape = RoundedCornerShape(8.dp)).height(height)
                    .padding(horizontal = 8.dp),
                value = value,
                textStyle = textStyle ?: MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamilies.openSans,
                    color = Color.Black
                ),
                keyboardOptions = keyboardOptions,
                onValueChange = onValueChange,
                singleLine = singleLine,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = verticalAlignment
                    ) {
                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty() && hint.isNotEmpty()) {
                                Text(
                                    hint,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        fontFamily = FontFamilies.openSans,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
        }
    }
}
