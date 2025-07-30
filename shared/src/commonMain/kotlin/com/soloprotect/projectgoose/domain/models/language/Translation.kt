package com.myprotect.projectx.domain.models.language

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    val active: Boolean? = null,
    val lastUpdatedUtc: Instant? = null,
    val translationId: Long? = null,
    val translationKey: String? = null,
    val translationLanguage: String? = null,
    val translationValue: String? = null
)
