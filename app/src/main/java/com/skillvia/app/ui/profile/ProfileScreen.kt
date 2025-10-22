package com.skillvia.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
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
import com.skillvia.app.ui.components.SkillCard 
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onAddSkillClick: () -> Unit,
    onManageRequestsClick: () -> Unit
) {
    val skillRepo = SkillRepo()
    val authRepository = AuthRepository()
    var currentUser by remember { mutableStateOf<User?>(null)}
    var userSkills by remember { mutableStateOf<List<Skill>>(emptyList())}
    var requestedSkillsWithStatus by remember { mutableStateOf<List<Pair<Skill, SkillRequest>>>(emptyList())}
    val scope = rememberCoroutineScope()

    // Load user data when screen first appears
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Get the logged-in user's ID from Supabase Auth
                val userId = authRepository.getCurrentUserId()
                
                if (userId != null) {
                    // Fetch user from Supabase
                    currentUser = skillRepo.getUserById(userId)
                    
                    if (currentUser != null) {
                        val allSkills = skillRepo.getAllSkills()
                        userSkills = allSkills.filter { it.providerID == currentUser?.id }
                        
                        // Fetch requested skills from skill_requests table in Supabase
                        val allRequests = skillRepo.getAllRequests()
                        val userRequests = allRequests.filter { it.requesterId == currentUser?.id }
                        
                        // Store pairs for status display
                        requestedSkillsWithStatus = userRequests.mapNotNull { request ->
                            val skill = allSkills.find { it.id == request.skillId }
                            skill?.let { Pair(it, request) }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error loading profile: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {Text("My Profile")},
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if(currentUser != null) {
            Column(
                modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
            ) {
                //user info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

                //Skills Offered Section
                Text(
                    text = "Skills I Offer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if(userSkills.isNotEmpty()) {
                    userSkills.forEach { skill ->
                        SkillCard(
                            skill = skill,
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "You haven't offered any skills yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Manage Requests Button (for skills I offer)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onManageRequestsClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "Manage Requests",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Requests")
                }
                
                //Add Skill Button
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

                //Skills Requested Section
                Text(
                    text = "Skills I've Requested",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if(requestedSkillsWithStatus.isNotEmpty()) {
                    requestedSkillsWithStatus.forEach { (skill, request) ->
                        // Show different card styles based on status
                        if (request.status == "ACCEPTED") {
                            // Accepted requests get a special card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
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
                                    
                                    // Show the message you sent
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
                                    
                                    // Info text
                                    Text(
                                        text = "💡 Coordinate meeting details with the provider through app messaging (coming soon) or contact them directly.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        } else {
                            // Pending/Rejected requests show simple card + status
                            Column {
                                SkillCard(
                                    skill = skill,
                                    onClick = { }
                                )
                                // Simple status text below the card
                                Text(
                                    text = "Status: ${request.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when(request.status) {
                                        "PENDING" -> MaterialTheme.colorScheme.secondary
                                        "REJECTED" -> MaterialTheme.colorScheme.error
                                        "COMPLETED" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.outline
                                    },
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
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

                //Edit Profile Button
                Button(
                    onClick = {
                        //TODO: Navigate to edit profile screen
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
}