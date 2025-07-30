package com.myprotect.projectx.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.myprotect.projectx.EnvConfig
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.extensions.replaceWithArgs
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.logo_white
import myprotect_mobile.shared.generated.resources.question

@Composable
fun AppTopBar(
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    val localeManager = koinInject<LocaleManager>()
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            .height(56.dp)
            .padding(horizontal = 18.dp).padding(bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            navigationIcon?.invoke() ?: {}
            Spacer(modifier = Modifier.weight(1.0f))
            Image(
                painter = painterResource(Res.drawable.logo_white),
                contentDescription = null,
                modifier = Modifier.width(197.dp).height(28.dp)
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(30.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)).clickable {
                        uriHandler.openUri(
                            EnvConfig.qsgLink.replaceWithArgs(
                                listOf(
                                    localeManager.languageCode.value
                                )
                            )
                        )
                    },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.question),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
