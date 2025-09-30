package com.skillvia.app.data.model

data class SkillRequest(
    val id: String = "",
    val skillId: String = "",
    val requesterId: String = "",
    val providerId: String = "",
    val message: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val requestedDate: Long = System.currentTimeMillis(),
    val acceptedDate: Long? = null,
    val completedDate: Long? = null,
    val cancelledDate: Long? = null,
    val meetingLocation: String = "",
    val meetingTime: Long? = null,
    val price: Double = 0.0,
    val requesterRating: Int? = null,
    val providerRating: Int? = null
)

enum class RequestStatus{
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    REJECTED
}
