package com.myprotect.projectx.data.datasource.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.db.AppDatabase

actual class SqlDelightDriverFactory {
    actual fun createDriver(context: Context): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, Constants.SQLITE_DB)
    }
}
