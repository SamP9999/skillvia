package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillCategory
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import com.skillvia.app.ui.components.SkillCard  // Import shared SkillCard component
import kotlinx.coroutines.launch

@Composable
fun SkillsListScreen(
    onSkillClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val authRepository = AuthRepository()
    val skillRepo = SkillRepo()  // Create repository instance
    var skills by remember { mutableStateOf<List<Skill>>(emptyList()) }  // State for skills list
    var showLogoutDialog by remember { mutableStateOf(false) }  // State for logout confirmation dialog
    val scope = rememberCoroutineScope()  // For async operations
    
    // Load skills when screen first appears
    LaunchedEffect(Unit) {
        scope.launch {
            skills = skillRepo.getAllSkills()  // Get all skills from repository
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Skillvia",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Skills List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(skills) { skill ->
                SkillCard(
                    skill = skill,
                    onClick = { onSkillClick(skill.id) }
                )
            }
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        scope.launch {
                            authRepository.signOut()
                            onLogout()
                        }
                    }
                ) {
                    Text("Yes, Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}