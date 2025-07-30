package com.myprotect.projectx.domain.models.risk_message

import com.myprotect.api.models.AcknowledgedStatus
import com.myprotect.api.models.RiskMessagesDto
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

@Serializable
data class RiskMessage(
    val messageId: String? = null,

    val timestampUtc: Long? = null,

    val messageType: String? = null,

    val subject: String? = null,

    val message: String? = null,

    val hasAttachment: Boolean? = null,

    val status: String? = null
) {
    companion object {
        const val STATUS_ACCEPTED = "Accepted"
        const val STATUS_OPENED = "Opened"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_DELETED = "Deleted"
    }

    val statusCode: AcknowledgedStatus
        get() {
            return when (status) {
                STATUS_ACCEPTED -> AcknowledgedStatus._0
                STATUS_OPENED -> AcknowledgedStatus._1
                STATUS_REJECTED -> AcknowledgedStatus._2
                STATUS_DELETED -> AcknowledgedStatus._3
                else -> AcknowledgedStatus._1
            }
        }
}

fun RiskMessagesDto.toDomain(): RiskMessage {
    return RiskMessage(
        messageId = this.messageId,
        timestampUtc = this.timestampUtc?.let {
            LocalDateTime.parse(it).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        },
        messageType = this.messageType,
        subject = this.subject,
        message = this.message,
        hasAttachment = this.hasAttachment,
        status = this.status
    )
}
