package com.skillvia.app.data.model

data class Skill(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: SkillCategory = SkillCategory.ACADEMIC,
    val price: Double = 0.0,
    val providerID: String = "",
    val providerName: String = "",
    val providerEmail: String = "",
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val rating: Float = 0.0f,
    val totalRatings: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class SkillCategory{
    ACADEMIC,
    CREATIVE_ARTS,
    FITNESS_LIFESTYLE,
    LIFE_SKILLS,
    TECHNOLOGY
}
