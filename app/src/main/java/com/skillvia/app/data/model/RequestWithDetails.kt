package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
//Data class for efficient request fetching with joins
@Serializable
data class RequestWithDetails(
    val id: String? = null,
    @SerialName("skill_id")
    val skillId: String = "",
    @SerialName("requester_id") 
    val requesterId: String = "",
    @SerialName("provider_id")
    val providerId: String = "",
    val message: String = "",
    val status: String = "PENDING",
    val price: Double = 0.0,
    val skills: SkillInfo? = null,
    val users: UserInfo? = null
)

@Serializable
data class SkillInfo(
    val title: String = ""
)

@Serializable  
data class UserInfo(
    val name: String = ""
)
