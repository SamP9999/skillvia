package com.skillvia.app.data.repository
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillCategory
import com.skillvia.app.data.model.User
import com.skillvia.app.data.model.SkillRequest
import com.skillvia.app.data.model.RequestStatus
import com.skillvia.app.utils.LocationUtils

class SkillRepo {
    // Sample skills data
    private val sampleSkills = listOf(
        Skill(
            id = "skill_001",
            title = "Math Tutoring",
            description = "Help with calculus, algebra, and statistics. I can assist with homework, exam prep, and understanding difficult concepts.",
            category = SkillCategory.ACADEMIC,
            price = 15.0,
            providerID = "user_001",
            providerName = "Alex Johnson",
            providerEmail = "alex.johnson@unb.ca",
            location = "UNB Campus Library",
            latitude = 45.9636,
            longitude = -66.6431,
            rating = 4.8f,
            totalRatings = 23,
            createdAt = "2025-01-16T10:00:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_002",
            title = "Guitar Lessons",
            description = "Learn basic guitar chords, strumming patterns, and simple songs. Perfect for beginners!",
            category = SkillCategory.CREATIVE_ARTS,
            price = 20.0,
            providerID = "user_002",
            providerName = "Sarah Chen",
            providerEmail = "sarah.chen@unb.ca",
            location = "UNB Music Building",
            latitude = 45.9640,
            longitude = -66.6425,
            rating = 4.9f,
            totalRatings = 15,
            createdAt = "2025-01-13T14:30:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_003",
            title = "Resume Writing",
            description = "Help with resume formatting, content optimization, and cover letter writing. I've helped 50+ students land internships!",
            category = SkillCategory.LIFE_SKILLS,
            price = 25.0,
            providerID = "user_003",
            providerName = "Michael Rodriguez",
            providerEmail = "michael.rodriguez@unb.ca",
            location = "Online",
            latitude = null,
            longitude = null,
            rating = 4.7f,
            totalRatings = 31,
            createdAt = "2025-01-17T09:15:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_004",
            title = "Python Programming",
            description = "Learn Python basics, data structures, and simple projects. Great for beginners or those wanting to improve their coding skills.",
            category = SkillCategory.TECHNOLOGY,
            price = 18.0,
            providerID = "user_004",
            providerName = "Emma Thompson",
            providerEmail = "emma.thompson@unb.ca",
            location = "UNB Computer Science Building",
            latitude = 45.9630,
            longitude = -66.6435,
            rating = 4.6f,
            totalRatings = 19,
            createdAt = "2025-01-15T11:00:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_005",
            title = "Personal Training",
            description = "Customized workout plans and fitness guidance. I can help you reach your fitness goals with proper form and motivation.",
            category = SkillCategory.FITNESS_LIFESTYLE,
            price = 22.0,
            providerID = "user_005",
            providerName = "David Kim",
            providerEmail = "david.kim@unb.ca",
            location = "UNB Fitness Center",
            latitude = 45.9645,
            longitude = -66.6420,
            rating = 4.5f,
            totalRatings = 12,
            createdAt = "2025-01-11T16:45:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_006",
            title = "English Essay Writing",
            description = "Help with essay structure, thesis development, and academic writing style. I can review drafts and provide feedback.",
            category = SkillCategory.ACADEMIC,
            price = 16.0,
            providerID = "user_006",
            providerName = "Lisa Wang",
            providerEmail = "lisa.wang@unb.ca",
            location = "UNB English Department",
            latitude = 45.9632,
            longitude = -66.6430,
            rating = 4.9f,
            totalRatings = 27,
            createdAt = "2025-01-14T13:20:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_007",
            title = "Photography Basics",
            description = "Learn camera settings, composition, and basic editing. Perfect for beginners who want to improve their photos.",
            category = SkillCategory.CREATIVE_ARTS,
            price = 20.0,
            providerID = "user_007",
            providerName = "James Wilson",
            providerEmail = "james.wilson@unb.ca",
            location = "Downtown Fredericton",
            latitude = 45.9650,
            longitude = -66.6415,
            rating = 4.4f,
            totalRatings = 8,
            createdAt = "2025-01-12T08:30:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_008",
            title = "Budgeting & Finance",
            description = "Learn personal finance basics, budgeting strategies, and investment fundamentals. Perfect for students managing money.",
            category = SkillCategory.LIFE_SKILLS,
            price = 14.0,
            providerID = "user_008",
            providerName = "Rachel Green",
            providerEmail = "rachel.green@unb.ca",
            location = "Online",
            latitude = null,
            longitude = null,
            rating = 4.8f,
            totalRatings = 16,
            createdAt = "2025-01-17T15:00:00Z",
            isActive = true
        )
    )

    // Sample users data
    private val sampleUsers = listOf(
        User(
            id = "user_001",
            name = "Alex Johnson",
            email = "alex.johnson@unb.ca",
            university = "University of New Brunswick",
            studentId = "12345678",
            phoneNumber = "(506) 555-0101",
            bio = "Math major with 3 years of tutoring experience. I love helping students understand complex concepts!",
            rating = 4.8f,
            totalRatings = 23,
            isVerified = true,
            createdAt = "2025-10-10T10:30:00Z",  
            lastActive = "2025-10-10T16:00:00Z"  
        ),
        User(
            id = "user_002",
            name = "Sarah Chen",
            email = "sarah.chen@unb.ca",
            university = "University of New Brunswick",
            studentId = "87654321",
            phoneNumber = "(506) 555-0102",
            bio = "Music student and guitar enthusiast. I've been playing for 8 years and love teaching beginners!",
            rating = 4.9f,
            totalRatings = 15,
            isVerified = true,
            createdAt = "2025-10-11T10:30:00Z",  
            lastActive = "2025-10-11T16:00:00Z" 
        ),
    )

    // Sample skill requests data
    private val sampleRequests = listOf(
        SkillRequest(
            id = "request_001",
            skillId = "skill_001",
            requesterId = "user_002",
            providerId = "user_001",
            message = "Hi Alex! I'm struggling with calculus and would love some help with integration. Are you available this weekend?",
            status = RequestStatus.PENDING,
            requestedDate = System.currentTimeMillis() - (2 * 60 * 60 * 1000L), // 2 hours ago
            price = 15.0
        ),
        SkillRequest(
            id = "request_002",
            skillId = "skill_002",
            requesterId = "user_001",
            providerId = "user_002",
            message = "Hi Sarah! I've always wanted to learn guitar. Do you have time for a lesson this week?",
            status = RequestStatus.ACCEPTED,
            requestedDate = System.currentTimeMillis() - (24 * 60 * 60 * 1000L), // 1 day ago
            acceptedDate = System.currentTimeMillis() - (20 * 60 * 60 * 1000L), // 20 hours ago
            meetingLocation = "UNB Music Building",
            meetingTime = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L), // 2 days from now
            price = 20.0
        )
    )

    // Repository methods
    suspend fun getAllSkills(): List<Skill> {
        return sampleSkills.filter { it.isActive }
    }

    suspend fun getSkillsByCategory(category: SkillCategory): List<Skill> {
        return sampleSkills.filter { it.category == category && it.isActive }
    }

    suspend fun getSkillById(id: String): Skill? {
        return sampleSkills.find { it.id == id }
    }

    suspend fun searchSkills(query: String): List<Skill> {
        return sampleSkills.filter { skill ->
            skill.isActive && (
                    skill.title.contains(query, ignoreCase = true) ||
                            skill.description.contains(query, ignoreCase = true) ||
                            skill.providerName.contains(query, ignoreCase = true)
                    )
        }
    }

    suspend fun getSkillsNearLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Skill> {
        return sampleSkills.filter { skill ->
            skill.isActive && skill.latitude != null && skill.longitude != null &&
                    LocationUtils.calculateDistance(latitude, longitude, skill.latitude, skill.longitude) <= radiusKm
        }
    }

    suspend fun getUserById(id: String): User? {
        return sampleUsers.find { it.id == id }
    }

    suspend fun getAllRequests(): List<SkillRequest> {
        return sampleRequests
    }

    suspend fun getRequestsByUserId(userId: String): List<SkillRequest> {
        return sampleRequests.filter {
            it.requesterId == userId || it.providerId == userId
        }
    }

    suspend fun createRequest(request: SkillRequest): SkillRequest {
        // In a real app, this would save to database
        // For now, just return the request
        return request
    }

    suspend fun updateRequestStatus(requestId: String, status: RequestStatus): Boolean {
        // In a real app, this would update the database
        // For now, just return true
        return true
    }
    // ===== DATA MANAGEMENT METHODS (for later) =====
    suspend fun addSkill(skill: Skill): Skill {
        // In a real app, this would save to database
        // For now, just return the skill
        return skill
    }

    suspend fun updateSkill(skill: Skill): Skill {
        // In a real app, this would update the database
        // For now, just return the skill
        return skill
    }

    suspend fun deleteSkill(id: String): Boolean {
        // In a real app, this would delete from database
        // For now, just return true
        return true
    }

}