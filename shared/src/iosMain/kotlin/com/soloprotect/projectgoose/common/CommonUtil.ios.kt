package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.stringWithContentsOfFile

actual fun randomUUID(): String = NSUUID().UUIDString()

actual fun readJsonFile(fileName: String, fileType: String, context: Context): String {
    return readJsonFromIOSFile(fileName, fileType)
}

@OptIn(ExperimentalForeignApi::class)
fun readJsonFromIOSFile(fileName: String, fileType: String): String {
    val path =
        NSBundle.mainBundle().pathForResource(fileName, fileType) ?: throw Exception("File not found")
    return  NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, null) as String
}

@Composable
actual fun ChangeStatusBarColors(statusBarColor: Color) {}
