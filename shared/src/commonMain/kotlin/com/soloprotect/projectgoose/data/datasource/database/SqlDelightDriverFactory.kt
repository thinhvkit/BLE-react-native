package com.myprotect.projectx.data.datasource.database

import app.cash.sqldelight.db.SqlDriver
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.db.AppDatabase

expect class SqlDelightDriverFactory() {
    fun createDriver(context: Context): SqlDriver
}

fun createDatabase(driver: SqlDriver): AppDatabase {
    return AppDatabase(driver)
}
