package com.skillvia.app.data.model

data class User (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val university: String = "",
    val studentId: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val skillsOffered: List<String> = emptyList(),
    val skillsRequested: List<String> = emptyList(),
    val rating: Float = 0.0f,
    val totalRatings: Int = 0,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis()
)
