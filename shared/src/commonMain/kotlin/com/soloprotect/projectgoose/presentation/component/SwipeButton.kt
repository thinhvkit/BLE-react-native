package com.myprotect.projectx.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BackgroundContent(endIcon: Painter) {
    Box {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = endIcon,
                contentDescription = "endIcon",
                tint = Color.White, modifier = Modifier.size(32.dp)
            )
            Spacer_4dp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeButton(
    doneImageVector: ImageVector = Icons.Rounded.Done,
    modifier: Modifier = Modifier,
    startIcon: Painter,
    endIcon: Painter,
    backgroundIconColor: Color = Color.LightGray,
    backgroundColor: Color,
    onSwipe: suspend () -> Unit,
) {

    val swipeComplete = rememberSaveable {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                StartToEnd -> {
                    if(!swipeComplete.value) {
                        swipeComplete.value = true
                        coroutineScope.launch(Dispatchers.IO) {
                            onSwipe()

                            delay(2000)
                            swipeComplete.value = false
                        }
                    }
                }

                else -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState false
        },
        positionalThreshold = { it * .75f })

    LaunchedEffect(swipeComplete) {
        if (!swipeComplete.value) {
            dismissState.reset()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp,
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black,
                        backgroundColor,
                    )
                )
            )
            .animateContentSize()
            .then(
                if (swipeComplete.value) {
                    Modifier.width(64.dp)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .requiredHeight(64.dp),
    ) {

        AnimatedVisibility(
            visible = !swipeComplete.value,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromEndToStart = false,
                modifier = modifier.fillMaxWidth(),
                backgroundContent = {
                    BackgroundContent(endIcon = endIcon)
                },
                content = {
                    SwipeIndicator(
                        icon = startIcon,
                        backgroundColor = backgroundIconColor
                    )
                })
        }

        AnimatedVisibility(
            visible = swipeComplete.value,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Icon(
                imageVector = doneImageVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(44.dp),
            )
        }
    }
}
