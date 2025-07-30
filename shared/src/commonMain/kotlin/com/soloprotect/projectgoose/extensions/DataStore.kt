package com.myprotect.projectx.extensions

import com.myprotect.projectx.common.Context

expect suspend fun Context.putData(key: String, `object`: String)

expect suspend fun Context.getData(key: String): String?

