package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Review(
    val id: String? = null,
    @SerialName("request_id")
    val requestId: String = "",
    @SerialName("requester_id")
    val requesterId: String = "",
    @SerialName("provider_id")
    val providerId: String = "",
    @SerialName("skill_id")
    val skillId: String = "",
    val rating: Int = 0, // 1-5
    val comment: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("requester_name")
    val requesterName: String? = null
)

