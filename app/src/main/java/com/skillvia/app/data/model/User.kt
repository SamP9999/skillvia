package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val university: String = "",
    @SerialName("student_id")
    val studentId: String = "",
    @SerialName("phone_number")
    val phoneNumber: String? = null,  // Nullable since it's optional in database
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,  // Nullable since it's optional in database
    val bio: String? = null,  // Nullable since it's optional in database
    val rating: Float = 0.0f,
    @SerialName("total_ratings")
    val totalRatings: Int = 0,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,  // Nullable since it might not be set
    @SerialName("last_active")
    val lastActive: String? = null  // Nullable since it might not be set
)
