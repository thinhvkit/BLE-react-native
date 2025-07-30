package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.UUID

actual fun randomUUID() = UUID.randomUUID().toString()

actual fun readJsonFile(fileName: String, fileType: String, context: Context): String {
    return readJsonFromAssets(context, "$fileName.$fileType")  // Adjust to your context
}

fun readJsonFromAssets(context: Context, fileName: String): String {
    val inputStream = context.assets.open(fileName)
    return inputStream.bufferedReader().use { it.readText() }
}

@Composable
actual fun ChangeStatusBarColors(statusBarColor: Color) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(statusBarColor)
}
