package com.skillvia.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.User
import com.skillvia.app.data.model.SkillRequest
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import com.skillvia.app.ui.theme.SkillviaCardDefaults
import com.skillvia.app.ui.components.SkillCard
import com.skillvia.app.ui.components.RatingDialog
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onAddSkillClick: () -> Unit,
    onManageRequestsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onEditSkillClick: (String) -> Unit = {}, // New callback for editing skills
    refreshTrigger: Int = 0 // Trigger to refresh data
) {
    val skillRepo = SkillRepo()
    val authRepository = AuthRepository()
    var currentUser by remember { mutableStateOf<User?>(null)}
    var userSkills by remember { mutableStateOf<List<Skill>>(emptyList())}
    var requestedSkillsWithStatus by remember { mutableStateOf<List<Pair<Skill, SkillRequest>>>(emptyList())}
    var requestRatings by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) } // requestId -> hasRating
    var pendingRequestCount by remember { mutableStateOf(0) } // Count of PENDING and ACCEPTED requests for provider
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedRequestForRating by remember { mutableStateOf<Pair<Skill, SkillRequest>?>(null) }
    var isSubmittingRating by remember { mutableStateOf(false) }
    var ratingSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var skillToDelete by remember { mutableStateOf<Skill?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteErrorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refreshTrigger, Unit) {
        scope.launch {
            try {
                val userId = authRepository.getCurrentUserId()
                
                if (userId != null) {
                    currentUser = skillRepo.getUserById(userId)
                    
                    if (currentUser != null) {
                        val allSkills = skillRepo.getAllSkills()
                        userSkills = allSkills.filter { it.providerID == currentUser?.id }
                        
                        val allRequests = skillRepo.getAllRequests()
                        val userRequests = allRequests.filter { it.requesterId == currentUser?.id }
                        
                        requestedSkillsWithStatus = userRequests.mapNotNull { request ->
                            val skill = allSkills.find { it.id == request.skillId }
                            skill?.let { Pair(it, request) }
                        }
                        
                        val providerRequests = allRequests.filter { 
                            it.providerId == currentUser?.id && 
                            (it.status == "PENDING" || it.status == "ACCEPTED")
                        }
                        pendingRequestCount = providerRequests.size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    LaunchedEffect(requestedSkillsWithStatus) {
        val completedRequests = requestedSkillsWithStatus.filter { it.second.status == "COMPLETED" }
        val ratingMap = mutableMapOf<String, Boolean>()
        completedRequests.forEach { (_, request) ->
            val requestId = request.id ?: ""
            if (requestId.isNotEmpty()) {
                val hasRating = skillRepo.hasRating(requestId)
                ratingMap[requestId] = hasRating
            }
        }
        requestRatings = ratingMap
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if(currentUser != null) {
            Column(
                modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                //user info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillviaCardDefaults.Shape,
                    elevation = SkillviaCardDefaults.elevation(),
                    colors = SkillviaCardDefaults.colors()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentUser!!.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentUser!!.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${String.format("%.1f", currentUser!!.rating)} (${currentUser!!.totalRatings})", //rating and count of reviews
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "University: ${currentUser!!.university}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if(!currentUser!!.bio.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUser!!.bio!!,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Skills I Offer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if(userSkills.isNotEmpty()) {
                    userSkills.forEach { skill ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = SkillviaCardDefaults.Shape,
                            elevation = SkillviaCardDefaults.elevation(),
                            colors = SkillviaCardDefaults.colors()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = skill.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "$${String.format("%.0f", skill.price)}/hr",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = skill.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "★ ${String.format("%.1f", skill.rating)} (${skill.totalRatings})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Row {
                                        IconButton(
                                            onClick = { onEditSkillClick(skill.id) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit Skill",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        IconButton(
                                            onClick = {
                                                skillToDelete = skill
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Skill",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "You haven't offered any skills yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onManageRequestsClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "Manage Requests",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Requests ($pendingRequestCount)")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAddSkillClick() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Skill",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Skill")
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Skills I've Requested",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if(requestedSkillsWithStatus.isNotEmpty()) {
                    requestedSkillsWithStatus.forEach { (skill, request) ->
                        if (request.status == "ACCEPTED") {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = SkillviaCardDefaults.Shape,
                                elevation = SkillviaCardDefaults.elevation(),
                                colors = SkillviaCardDefaults.colors()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = skill.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = skill.providerName ?: "Provider",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        Text(
                                            text = "$${String.format("%.0f", skill.price)}/hr",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Text(
                                        text = "Your Message:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = request.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Status badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Accepted",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ACCEPTED",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "💡 Coordinate meeting details with the provider through app messaging (coming soon) or contact them directly.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        } else if (request.status == "COMPLETED") {
                            val requestId = request.id ?: ""
                            val hasRating = requestRatings[requestId] ?: false
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = SkillviaCardDefaults.Shape,
                                elevation = SkillviaCardDefaults.elevation(),
                                colors = SkillviaCardDefaults.colors()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = skill.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = skill.providerName ?: "Provider",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        Text(
                                            text = "$${String.format("%.0f", skill.price)}/hr",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Status badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Completed",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "COMPLETED",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    if (hasRating) {
                                        // Already rated
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = "Rated",
                                                    modifier = Modifier.size(18.dp),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "You've already rated this provider",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    } else {
                                        // Rate Provider button
                                        Button(
                                            onClick = {
                                                selectedRequestForRating = Pair(skill, request)
                                                showRatingDialog = true
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Rate",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Rate Provider")
                                        }
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = SkillviaCardDefaults.Shape,
                                elevation = SkillviaCardDefaults.elevation(),
                                colors = SkillviaCardDefaults.colors()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = skill.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = skill.providerName ?: "Provider",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        Text(
                                            text = "$${String.format("%.0f", skill.price)}/hr",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Status badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (request.status == "REJECTED") {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = request.status,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = request.status,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = when(request.status) {
                                                "PENDING" -> MaterialTheme.colorScheme.secondary
                                                "REJECTED" -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.outline
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "You haven't requested any skills yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onEditProfileClick() // Navigate to edit profile screen
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))    
            }
        }
        else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    
    if (showRatingDialog && selectedRequestForRating != null) {
        val (skill, request) = selectedRequestForRating!!
        RatingDialog(
            providerName = skill.providerName ?: "Provider",
            skillTitle = skill.title,
            onDismiss = {
                showRatingDialog = false
                selectedRequestForRating = null
            },
            onSubmit = { rating, comment ->
                isSubmittingRating = true
                scope.launch {
                    val userId = authRepository.getCurrentUserId()
                    if (userId != null && request.id != null) {
                        val result = skillRepo.submitRating(
                            requestId = request.id,
                            requesterId = userId,
                            providerId = skill.providerID,
                            skillId = skill.id,
                            rating = rating,
                            comment = comment.ifBlank { null }
                        )
                        
                        if (result.isSuccess) {
                            ratingSuccessMessage = "Rating submitted successfully!"
                            requestRatings = requestRatings + (request.id to true)
                            // Refresh user data to show updated ratings
                            currentUser = skillRepo.getUserById(userId)
                            showRatingDialog = false
                            selectedRequestForRating = null
                        } else {
                            ratingSuccessMessage = "Error: ${result.exceptionOrNull()?.message ?: "Failed to submit rating"}"
                        }
                        isSubmittingRating = false
                    }
                }
            },
            isLoading = isSubmittingRating
        )
    }
    
    if (showDeleteDialog && skillToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                    skillToDelete = null
                    deleteErrorMessage = ""
                }
            },
            title = { Text("Delete Skill?") },
            text = {
                Column {
                    Text("Are you sure you want to delete \"${skillToDelete?.title}\"?")
                    if (deleteErrorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = deleteErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val skill = skillToDelete ?: return@Button
                        isDeleting = true
                        deleteErrorMessage = ""
                        
                        scope.launch {
                            val result = skillRepo.deleteSkill(skill.id)
                            if (result.isSuccess) {
                                // Refresh skills list from database
                                val userId = authRepository.getCurrentUserId()
                                if (userId != null) {
                                    val allSkills = skillRepo.getAllSkills()
                                    userSkills = allSkills.filter { it.providerID == userId }
                                } else {
                                    userSkills = userSkills.filter { it.id != skill.id }
                                }
                                showDeleteDialog = false
                                skillToDelete = null
                            } else {
                                deleteErrorMessage = result.exceptionOrNull()?.message 
                                    ?: "Failed to delete skill"
                            }
                            isDeleting = false
                        }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        skillToDelete = null
                        deleteErrorMessage = ""
                    },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    ratingSuccessMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            ratingSuccessMessage = null
        }
    }
    if (ratingSuccessMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar {
                Text(ratingSuccessMessage!!)
            }
        }
    }
}