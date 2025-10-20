package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SkillRequest(
    val id: String? = null,  
    @SerialName("skill_id")
    val skillId: String = "",
    @SerialName("requester_id")
    val requesterId: String = "",
    @SerialName("provider_id")
    val providerId: String = "",
    val message: String = "",
    val status: String = "PENDING",  // Store as String in database
    @SerialName("requested_date")
    val requestedDate: String? = null,  
    @SerialName("accepted_date")
    val acceptedDate: String? = null,
    @SerialName("completed_date")
    val completedDate: String? = null,
    @SerialName("cancelled_date")
    val cancelledDate: String? = null,
    @SerialName("meeting_location")
    val meetingLocation: String? = null,
    @SerialName("meeting_time")
    val meetingTime: String? = null,
    val price: Double = 0.0,
    @SerialName("requester_rating")
    val requesterRating: Int? = null,
    @SerialName("provider_rating")
    val providerRating: Int? = null
)

enum class RequestStatus{
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    REJECTED
}
