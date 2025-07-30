package com.myprotect.projectx.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.theme.SecondaryColor
import com.myprotect.projectx.presentation.theme.green_01

@Composable
fun PermissionItem(
    label: String,
    desc: String,
    selected: Boolean,
    icon: Painter,
    onChanged: (value: Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = icon,
            null,
            modifier = Modifier.size(40.dp),
            tint = SecondaryColor
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamilies.futuraBold
                    ),
                    color = Color.Black
                )
                Box(modifier = Modifier.clickable { onChanged(true) }) {
                    Checkbox(
                        enabled = false,
                        checked = selected,
                        colors = CheckboxDefaults.colors(
                            checkedColor = green_01,
                            disabledCheckedColor = green_01,
                        ),
                        onCheckedChange = null
                    )
                }
            }
            Text(desc, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
