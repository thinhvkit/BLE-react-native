package com.myprotect.projectx.domain.core

import com.myprotect.projectx.common.Context
import com.myprotect.projectx.extensions.getData
import com.myprotect.projectx.extensions.putData

const val APP_DATASTORE = "com.myprotect.projectx"

class AppDataStoreManager(val context: Context) : AppDataStore {

    override suspend fun setValue(
        key: String,
        value: String
    ) {
        context.putData(key, value)
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.getData(key)
    }
}
