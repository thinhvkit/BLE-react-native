package com.myprotect.projectx.domain.models.profile

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val country: String? = null,
    val dateOfBirth: LocalDateTime? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val locale: String? = null,
    val medicalIssues: String? = null,
    val timeZone: String? = null
) {
    val fullName: String
        get() = listOf(firstName, lastName).filterNotNull().joinToString(" ").trim()
}
