package com.skillvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Skill(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: SkillCategory = SkillCategory.ACADEMIC,
    val price: Double = 0.0,
    @SerialName("provider_id")
    val providerID: String = "",
    @SerialName("provider_name")
    val providerName: String = "",
    @SerialName("provider_email")
    val providerEmail: String = "",
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
