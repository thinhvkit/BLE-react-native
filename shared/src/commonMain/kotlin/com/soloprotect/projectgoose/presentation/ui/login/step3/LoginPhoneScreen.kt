package com.myprotect.projectx.presentation.ui.login.step3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.common.CommonUtil.Companion.isNumberOnly
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.presentation.component.AppTopBar
import com.myprotect.projectx.presentation.component.ButtonLoading
import com.myprotect.projectx.presentation.component.Spacer_14dp
import com.myprotect.projectx.presentation.component.Spacer_20dp
import com.myprotect.projectx.presentation.component.Spacer_26dp
import com.myprotect.projectx.presentation.component.Spacer_4dp
import com.myprotect.projectx.presentation.theme.FontFamilies
import com.myprotect.projectx.presentation.ui.login.component.InfoRow
import com.myprotect.projectx.presentation.ui.login.step3.view_model.LoginPhoneEvent
import com.myprotect.projectx.presentation.ui.login.step3.view_model.LoginPhoneState
import org.jetbrains.compose.resources.painterResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.edit
import myprotect_mobile.shared.generated.resources.gb
import myprotect_mobile.shared.generated.resources.info_circle_grey
import myprotect_mobile.shared.generated.resources.lock_alt
import myprotect_mobile.shared.generated.resources.phone_alt

@Composable
fun LoginPhoneScreen(
    state: LoginPhoneState,
    events: (LoginPhoneEvent) -> Unit,
    navigateToNext: () -> Unit,
    back: () -> Unit
) {
    LaunchedEffect(state.isNavigateNext) {
        if (state.isNavigateNext) {
            navigateToNext()
        }
    }
    var phone by rememberSaveable { mutableStateOf("") }
    val localFocusManager = LocalFocusManager.current
    Scaffold(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            localFocusManager.clearFocus()
        })
    }, topBar = {
        AppTopBar(
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).clickable { back() }
                )
            }
        )
    }, content = {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.padding(top = 64.dp, start = 20.dp, end = 20.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.phone_alt),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = Color(0xFF3389DF)),
                            modifier = Modifier.height(18.dp)
                        )
                        Spacer_4dp()
                        Text(
                            LocaleKeys.EMERGENCY_PHONE_TITLE.tr(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                fontFamily = FontFamilies.futuraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    Spacer_14dp()
                    InfoRow(
                        resource = Res.drawable.lock_alt,
                        text = LocaleKeys.WE_STORE_CONFIDENTIALLY.tr()
                    )
                    Spacer_26dp()
                    InfoRow(
                        resource = Res.drawable.info_circle_grey,
                        text = LocaleKeys.SELECT_LOCALE_AND_PHONE.tr()
                    )
                    Spacer_26dp()
                    Text(
                        LocaleKeys.EMERGENCY_PHONE_TITLE.tr(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontFamily = FontFamilies.openSans,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer_14dp()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(45.dp)
                                .clip(shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                .background(Color(0xFFDFDFDF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.gb),
                                contentDescription = "UK",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        BasicTextField(
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            modifier = Modifier.weight(1f).background(color = Color.White).border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                            ).clip(shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                .height(45.dp)
                                .padding(horizontal = 8.dp),
                            value = phone,
                            onValueChange = {
                                if (it.isEmpty() || isNumberOnly(it)) {
                                    phone = it
                                }
                            },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.weight(1f)) {
                                        innerTextField()
                                    }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonLoading(
                        enabled = phone.isNotEmpty(),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            events(LoginPhoneEvent.VerifyPhoneNumber(phone))
                        },
                        modifier = Modifier.width(168.dp).height(60.dp),
                        progressBarState = state.progressBarState,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                        )
                    ) {
                        Text(
                            LocaleKeys.VERIFY.tr(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp,
                                fontFamily = FontFamilies.futuraBold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer_20dp()
                }
            }
            AnimatedVisibility(
                visible = state.message != null, enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 56.dp)
                        .defaultMinSize(minHeight = 60.dp)
                        .background(Color(0xFFB61E33)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.edit),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = Color.White),
                            modifier = Modifier.width(42.dp)
                        )
                        Spacer_4dp()
                        Text(
                            state.message?.message?.tr() ?: "",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamilies.openSans,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    })
}
