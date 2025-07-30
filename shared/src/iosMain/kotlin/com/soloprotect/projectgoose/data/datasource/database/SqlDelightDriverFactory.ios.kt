package com.myprotect.projectx.data.datasource.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.db.AppDatabase

actual class SqlDelightDriverFactory {
    actual fun createDriver(context: Context): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, Constants.SQLITE_DB)
    }
}
