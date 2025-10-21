package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Skill(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("category")
    val category: String = "ACADEMIC",
    val price: Double = 0.0,
    @SerialName("provider_id")
    val providerID: String = "",
    val providerName: String? = null, 
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val rating: Float = 0.0f,
    @SerialName("total_ratings")
    val totalRatings: Int = 0,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String = ""
)

enum class SkillCategory{
    ACADEMIC,
    CREATIVE_ARTS,
    FITNESS_LIFESTYLE,
    LIFE_SKILLS,
    TECHNOLOGY
}
