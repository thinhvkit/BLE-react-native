package com.myprotect.projectx.presentation.ui.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprotect.projectx.presentation.component.Spacer_6dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun InfoRow(resource: DrawableResource, text: String, tintColor: Color? = null) {
    Box(
        modifier = Modifier.clip(shape = RoundedCornerShape(8.dp))
            .defaultMinSize(minHeight = 58.dp)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(resource),
                contentDescription = null,
                colorFilter = tintColor?.run { ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.height(18.dp)
            )
            Spacer_6dp()
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamilies.openSans,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
