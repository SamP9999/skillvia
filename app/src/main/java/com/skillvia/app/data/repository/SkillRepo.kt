package com.skillvia.app.data.repository
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillCategory
import com.skillvia.app.data.model.User
import com.skillvia.app.data.model.SkillRequest
import com.skillvia.app.data.model.RequestStatus
import com.skillvia.app.data.model.RequestWithDetails
import com.skillvia.app.data.model.SkillInfo
import com.skillvia.app.data.model.UserInfo
import com.skillvia.app.data.model.Review
import com.skillvia.app.data.supabase.SupabaseClient
import com.skillvia.app.utils.LocationUtils
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

class SkillRepo {
    private val supabase = SupabaseClient.client
    private val sampleSkills = listOf(
        Skill(
            id = "550e8400-e29b-41d4-a716-446655440001",
            title = "Math Tutoring",
            description = "Help with calculus, algebra, and statistics. I can assist with homework, exam prep, and understanding difficult concepts.",
            category = "ACADEMIC",
            price = 15.0,
            providerID = "550e8400-e29b-41d4-a716-446655440011",
            providerName = "Alex Johnson",
            location = "UNB Campus Library",
            latitude = 45.9636f,
            longitude = -66.6431f,
            rating = 4.8f,
            totalRatings = 23,
            createdAt = "2025-01-16T10:00:00Z",
            isActive = true
        ),
        Skill(
            id = "550e8400-e29b-41d4-a716-446655440002",
            title = "Guitar Lessons",
            description = "Learn basic guitar chords, strumming patterns, and simple songs. Perfect for beginners!",
            category = "CREATIVE_ARTS",
            price = 20.0,
            providerID = "550e8400-e29b-41d4-a716-446655440012",
            providerName = "Sarah Chen",
            location = "UNB Music Building",
            latitude = 45.9640f,
            longitude = -66.6425f,
            rating = 4.9f,
            totalRatings = 15,
            createdAt = "2025-01-13T14:30:00Z",
            isActive = true
        ),
        Skill(
            id = "550e8400-e29b-41d4-a716-446655440003",
            title = "Resume Writing",
            description = "Help with resume formatting, content optimization, and cover letter writing. I've helped 50+ students land internships!",
            category = "LIFE_SKILLS",
            price = 25.0,
            providerID = "550e8400-e29b-41d4-a716-446655440013",
            providerName = "Michael Rodriguez",
            location = "Online",
            latitude = null,
            longitude = null,
            rating = 4.7f,
            totalRatings = 31,
            createdAt = "2025-01-17T09:15:00Z",
            isActive = true
        ),
        Skill(
            id = "550e8400-e29b-41d4-a716-446655440004",
            title = "Python Programming",
            description = "Learn Python basics, data structures, and simple projects. Great for beginners or those wanting to improve their coding skills.",
            category = "TECHNOLOGY",
            price = 18.0,
            providerID = "550e8400-e29b-41d4-a716-446655440014",
            providerName = "Emma Thompson",
            location = "UNB Computer Science Building",
            latitude = 45.9630f,
            longitude = -66.6435f,
            rating = 4.6f,
            totalRatings = 19,
            createdAt = "2025-01-15T11:00:00Z",
            isActive = true
        ),
        Skill(
            id = "550e8400-e29b-41d4-a716-446655440005",
            title = "Personal Training",
            description = "Customized workout plans and fitness guidance. I can help you reach your fitness goals with proper form and motivation.",
            category = "FITNESS_LIFESTYLE",
            price = 22.0,
            providerID = "550e8400-e29b-41d4-a716-446655440015",
            providerName = "David Kim",
            location = "UNB Fitness Center",
            latitude = 45.9645f,
            longitude = -66.6420f,
            rating = 4.5f,
            totalRatings = 12,
            createdAt = "2025-01-11T16:45:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_006",
            title = "English Essay Writing",
            description = "Help with essay structure, thesis development, and academic writing style. I can review drafts and provide feedback.",
            category = "ACADEMIC",
            price = 16.0,
            providerID = "user_006",
            location = "UNB English Department",
            latitude = 45.9632f,
            longitude = -66.6430f,
            rating = 4.9f,
            totalRatings = 27,
            createdAt = "2025-01-14T13:20:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_007",
            title = "Photography Basics",
            description = "Learn camera settings, composition, and basic editing. Perfect for beginners who want to improve their photos.",
            category = "CREATIVE_ARTS",
            price = 20.0,
            providerID = "user_007",
            location = "Downtown Fredericton",
            latitude = 45.9650f,
            longitude = -66.6415f,
            rating = 4.4f,
            totalRatings = 8,
            createdAt = "2025-01-12T08:30:00Z",
            isActive = true
        ),
        Skill(
            id = "skill_008",
            title = "Budgeting & Finance",
            description = "Learn personal finance basics, budgeting strategies, and investment fundamentals. Perfect for students managing money.",
            category = "LIFE_SKILLS",
            price = 14.0,
            providerID = "user_008",
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
            id = "550e8400-e29b-41d4-a716-446655440101",
            skillId = "550e8400-e29b-41d4-a716-446655440001",
            requesterId = "550e8400-e29b-41d4-a716-446655440012",
            providerId = "550e8400-e29b-41d4-a716-446655440011",
            message = "Hi Alex! I'm struggling with calculus and would love some help with integration. Are you available this weekend?",
            status = "PENDING",
            requestedDate = "2024-01-15T10:00:00Z", // 2 hours ago
            price = 15.0
        ),
        SkillRequest(
            id = "550e8400-e29b-41d4-a716-446655440102",
            skillId = "550e8400-e29b-41d4-a716-446655440002",
            requesterId = "550e8400-e29b-41d4-a716-446655440011",
            providerId = "550e8400-e29b-41d4-a716-446655440012",
            message = "Hi Sarah! I've always wanted to learn guitar. Do you have time for a lesson this week?",
            status = "ACCEPTED",
            requestedDate = "2024-01-14T10:00:00Z", 
            acceptedDate = "2024-01-14T14:00:00Z", 
            meetingLocation = "UNB Music Building",
            meetingTime = "2024-01-17T10:00:00Z", 
            price = 20.0
        )
    )

    // Repository methods
    suspend fun getAllSkills(): List<Skill> {
        return try {
            val skills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            val users = supabase.from("users")
                .select()
                .decodeList<User>() 
            val userMap = users.associateBy { it.id }
            
            val skillsWithProviders = skills.map { skill ->
                skill.copy(providerName = userMap[skill.providerID]?.name)
            }
            
            skillsWithProviders.filter { it.isActive }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to sample data if Supabase fails
            sampleSkills.filter { it.isActive }
        }
    }

    suspend fun getSkillsByCategory(category: SkillCategory): List<Skill> {
        return try {
            val skills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            skills.filter { it.category == category.name && it.isActive }
        } catch (e: Exception) {
            e.printStackTrace()
            sampleSkills.filter { it.category == category.name && it.isActive }
        }
    }

    suspend fun getSkillById(id: String): Skill? {
        return try {
            val skills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            skills.find { it.id == id }
        } catch (e: Exception) {
            e.printStackTrace()
            sampleSkills.find { it.id == id }
        }
    }

    suspend fun searchSkills(query: String): List<Skill> {
        return try {
            val skills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            skills.filter { skill ->
                skill.isActive && (
                    skill.title.contains(query, ignoreCase = true) ||
                    skill.description.contains(query, ignoreCase = true)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sampleSkills.filter { skill ->
                skill.isActive && (
                    skill.title.contains(query, ignoreCase = true) ||
                    skill.description.contains(query, ignoreCase = true)
                )
            }
        }
    }

    suspend fun getSkillsNearLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Skill> {
        return try {
            val skills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            skills.filter { skill ->
                skill.isActive && skill.latitude != null && skill.longitude != null &&
                LocationUtils.calculateDistance(latitude, longitude, skill.latitude!!.toDouble(), skill.longitude!!.toDouble()) <= radiusKm
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sampleSkills.filter { skill ->
                skill.isActive && skill.latitude != null && skill.longitude != null &&
                LocationUtils.calculateDistance(latitude, longitude, skill.latitude!!.toDouble(), skill.longitude!!.toDouble()) <= radiusKm
            }
        }
    }

    suspend fun getUserById(id: String): User? {
        return try {
            val users = supabase.from("users")
                .select()
                .decodeList<User>()
            
            users.find { it.id == id }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllRequests(): List<SkillRequest> {
        return try {
            val requests = supabase.from("skill_requests")
                .select()
                .decodeList<SkillRequest>()
            
            requests 
        } catch (e: Exception) {
            e.printStackTrace()
            sampleRequests
        }
    }

    suspend fun getRequestsByUserId(userId: String): List<SkillRequest> {
        return try {
            val requests = supabase.from("skill_requests")
                .select()
                .decodeList<SkillRequest>()
            
            requests.filter { it.requesterId == userId || it.providerId == userId }
        } catch (e: Exception) {
            e.printStackTrace()
            sampleRequests.filter {
                it.requesterId == userId || it.providerId == userId
            }
        }
    }

    suspend fun createRequest(request: SkillRequest): Result<SkillRequest> {
        return try {
            supabase.from("skill_requests").insert(request)
            Result.success(request)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateRequestStatus(requestId: String, status: String): Boolean {
        return try {
            supabase.from("skill_requests")
                .update(
                    {
                        set("status", status)
                    }
                ) {
                    filter {
                        eq("id", requestId) 
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addSkill(skill: Skill): Result<Skill> {
        return try {
         @Serializable
            data class SkillInsert(
                val title: String,
                val description: String,
                val category: String,
                val price: Double,
                @SerialName("provider_id")
                val providerId: String,
                val location: String,
                val latitude: Float? = null,
                val longitude: Float? = null,
                @SerialName("delivery_type")
                val deliveryType: String = "BOTH",  
                val rating: Float = 0.0f,
                @SerialName("total_ratings")
                val totalRatings: Int = 0,
                @SerialName("is_active")
                val isActive: Boolean = true
            )
            
                val skillData = SkillInsert(
                title = skill.title,
                description = skill.description,
                category = skill.category,
                price = skill.price,
                providerId = skill.providerID,
                location = skill.location,
                latitude = skill.latitude,
                longitude = skill.longitude,
                deliveryType = skill.deliveryType, 
                rating = skill.rating,
                totalRatings = skill.totalRatings,
                isActive = skill.isActive
            )
            
            supabase.from("skills")
                .insert(skillData)
            
            Result.success(skill)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateSkill(skill: Skill): Result<Skill> {
        return try {
            if (skill.id.isEmpty()) {
                return Result.failure(Exception("Skill ID is required"))
            }
            
            @Serializable
            data class SkillUpdate(
                val title: String,
                val description: String,
                val category: String,
                val price: Double,
                val location: String,
                val latitude: Float?,
                val longitude: Float?,
                @SerialName("delivery_type")
                val deliveryType: String
            )
            
            val updateData = SkillUpdate(
                title = skill.title,
                description = skill.description,
                category = skill.category,
                price = skill.price,
                location = skill.location,
                latitude = skill.latitude,
                longitude = skill.longitude,
                deliveryType = skill.deliveryType
            )
            
            supabase.from("skills")
                .update(updateData) {
                    filter {
                        eq("id", skill.id)
                    }
                }
            
            Result.success(skill)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun hasActiveRequests(skillId: String): Boolean {
        return try {
            val requests = supabase.from("skill_requests")
                .select()
                .decodeList<SkillRequest>()
            
            requests.any { 
                it.skillId == skillId && 
                (it.status == "PENDING" || it.status == "ACCEPTED" || it.status == "IN_PROGRESS")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    suspend fun deleteSkill(id: String): Result<Boolean> {
        return try {
            if (id.isEmpty()) {
                return Result.failure(Exception("Skill ID is required"))
            }
            
            if (hasActiveRequests(id)) {
                return Result.failure(Exception("Cannot delete skill with active requests. Please wait for all requests to be completed or rejected."))
            }
            supabase.from("skills")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun getRequestsWithDetails(providerId: String): List<RequestWithDetails> {
        return try {
            val allRequests = supabase.from("skill_requests")
                .select()
                .decodeList<SkillRequest>()
            
            val allSkills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            val allUsers = supabase.from("users")
                .select()
                .decodeList<User>()
            
            val filteredRequests = allRequests.filter { 
                it.providerId == providerId && it.status == "PENDING" 
            }
            
            filteredRequests.map { request ->
                val skill = allSkills.find { it.id == request.skillId }
                val user = allUsers.find { it.id == request.requesterId }
                
                RequestWithDetails(
                    id = request.id,
                    skillId = request.skillId,
                    requesterId = request.requesterId,
                    providerId = request.providerId,
                    message = request.message,
                    status = request.status,
                    price = request.price,
                    skills = skill?.let { SkillInfo(title = it.title) },
                    users = user?.let { UserInfo(name = it.name) }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getAllRequestsForProvider(providerId: String): List<RequestWithDetails> {
        return try {
            val allRequests = supabase.from("skill_requests")
                .select()
                .decodeList<SkillRequest>()
            
            val allSkills = supabase.from("skills")
                .select()
                .decodeList<Skill>()
            
            val allUsers = supabase.from("users")
                .select()
                .decodeList<User>()
            
            val filteredRequests = allRequests.filter { 
                it.providerId == providerId && (
                    it.status == "PENDING" ||
                    it.status == "ACCEPTED" ||
                    it.status == "COMPLETED"
                )
            }
            
            filteredRequests.map { request ->
                val skill = allSkills.find { it.id == request.skillId }
                val user = allUsers.find { it.id == request.requesterId }
                
                RequestWithDetails(
                    id = request.id,
                    skillId = request.skillId,
                    requesterId = request.requesterId,
                    providerId = request.providerId,
                    message = request.message,
                    status = request.status,
                    price = request.price,
                    skills = skill?.let { SkillInfo(title = it.title) },
                    users = user?.let { UserInfo(name = it.name) }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun submitRating(
        requestId: String,
        requesterId: String,
        providerId: String,
        skillId: String,
        rating: Int,
        comment: String?
    ): Result<Unit> {
        return try {
            val allReviews = supabase.from("reviews")
                .select()
                .decodeList<Review>()
            val existingReviews = allReviews.filter { it.requestId == requestId }
            
            if (existingReviews.isNotEmpty()) {
                return Result.failure(Exception("You have already rated this provider"))
            }
            
            @Serializable
            data class ReviewInsert(
                @SerialName("request_id")
                val requestId: String,
                @SerialName("requester_id")
                val requesterId: String,
                @SerialName("provider_id")
                val providerId: String,
                @SerialName("skill_id")
                val skillId: String,
                val rating: Int,
                val comment: String? = null
            )
            
            val reviewData = ReviewInsert(
                requestId = requestId,
                requesterId = requesterId,
                providerId = providerId,
                skillId = skillId,
                rating = rating,
                comment = comment?.takeIf { it.isNotBlank() }
            )
            
            supabase.from("reviews").insert(reviewData)
            
            try {
                updateUserRating(providerId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            try {
                updateSkillRating(skillId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private suspend fun updateUserRating(userId: String) {
        try {
            val allReviewsRaw = supabase.from("reviews")
                .select()
                .decodeList<Review>()
            val allReviews = allReviewsRaw.filter { it.providerId == userId }
            
            if (allReviews.isEmpty()) {
                supabase.from("users").update(
                    {
                        set("rating", 0.0f)
                        set("total_ratings", 0)
                    }
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
                return
            }
            
            val averageRating = allReviews.map { review -> review.rating }.average().toFloat()
            val totalRatings = allReviews.size
            
            supabase.from("users").update(
                {
                    set("rating", averageRating)
                    set("total_ratings", totalRatings)
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun updateSkillRating(skillId: String) {
        try {
            val allReviewsRaw = supabase.from("reviews")
                .select()
                .decodeList<Review>()
            val allReviews = allReviewsRaw.filter { it.skillId == skillId }
            
            val averageRating = if (allReviews.isEmpty()) {
                0.0f
            } else {
                allReviews.map { review -> review.rating }.average().toFloat()
            }
            val totalRatings = allReviews.size
            
            try {
                supabase.from("skills").update(
                    {
                        set("rating", averageRating)
                        set("total_ratings", totalRatings)
                    }
                ) {
                    filter {
                        eq("id", skillId)
                    }
                }
            } catch (updateError: Exception) {
                updateError.printStackTrace()
                throw updateError
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun recalculateSkillRating(skillId: String): Result<Unit> {
        return try {
            updateSkillRating(skillId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun hasRating(requestId: String): Boolean {
        return try {
            val allReviews = supabase.from("reviews")
                .select()
                .decodeList<Review>()
            val reviews = allReviews.filter { it.requestId == requestId }
            reviews.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getReviewsForSkill(skillId: String): List<Review> {
        return try {
            val allReviews = supabase.from("reviews")
                .select()
                .decodeList<Review>()
            val filteredReviews = allReviews.filter { it.skillId == skillId }
                .sortedByDescending { it.createdAt ?: "" }
            
            val allUsers = supabase.from("users")
                .select()
                .decodeList<User>()
            val userMap = allUsers.associateBy { it.id }
            
            filteredReviews.map { review ->
                val requesterName = userMap[review.requesterId]?.name
                review.copy(requesterName = requesterName)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}