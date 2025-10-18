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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.User
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import com.skillvia.app.ui.components.SkillCard 
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit
) {
    val skillRepo = SkillRepo()
    val authRepository = AuthRepository()
    var currentUser by remember { mutableStateOf<User?>(null)}
    var userSkills by remember { mutableStateOf<List<Skill>>(emptyList())}
    var requestedSkills by remember { mutableStateOf<List<Skill>>(emptyList())}
    val scope = rememberCoroutineScope()

    // Load user data when screen first appears
    LaunchedEffect(Unit) {
        scope.launch {
            // Get the ACTUAL logged-in user's ID from Supabase Auth
            val userId = authRepository.getCurrentUserId() ?: "user_001"  // Fallback to user_001 if not logged in
            
            currentUser = skillRepo.getUserById(userId)  // ← Use real user ID
            val allSkills = skillRepo.getAllSkills()
            userSkills = allSkills.filter { it.providerID == currentUser?.id }
            
            val requestedSkillIds = listOf("skill_004", "skill_007") // TODO: Get actual requested skills from user profile, hardcoded for now
            requestedSkills = allSkills.filter { it.id in requestedSkillIds }
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
                        if(currentUser!!.bio.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUser!!.bio,
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
                Spacer(modifier = Modifier.height(24.dp))

                //Skills Requested Section
                Text(
                    text = "Skills I've Requested",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if(requestedSkills.isNotEmpty()) {
                    requestedSkills.forEach { skill ->
                        SkillCard(
                            skill = skill,
                            onClick = { }
                        )
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
        
        
