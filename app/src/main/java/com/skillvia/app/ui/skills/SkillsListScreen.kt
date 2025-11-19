package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.skillvia.app.ui.components.SkillCard  
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip

@Composable
fun SkillsListScreen(
    onSkillClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val authRepository = AuthRepository()
    val skillRepo = SkillRepo()  // Create repository instance
    var allSkills by remember { mutableStateOf<List<Skill>>(emptyList()) }  // State for skills list
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<SkillCategory?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }  // State for logout confirmation dialog
    val scope = rememberCoroutineScope()  // For async operations
    
    // Load skills when screen first appears
    LaunchedEffect(Unit) {
        scope.launch {
            allSkills = skillRepo.getAllSkills() 
        }
    }
    val filteredSkills = remember(allSkills, searchQuery, selectedCategory) {
        allSkills.filter { skill ->
            val matchesSearch = searchQuery.isEmpty() || 
                skill.title.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || skill.category == selectedCategory?.name
            // skill has to match both search and category
            matchesSearch && matchesCategory
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
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        //Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search skills...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        //Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null},
                    label = { Text("All")}
                )
            }
            item {
                //academic filter
                FilterChip(
                    selected = selectedCategory == SkillCategory.ACADEMIC,
                    onClick = { selectedCategory = SkillCategory.ACADEMIC},
                    label = { Text("Academic")}
                )
            }
            item {
                // Technology category chip
                FilterChip(
                    selected = selectedCategory == SkillCategory.TECHNOLOGY,
                    onClick = { selectedCategory = SkillCategory.TECHNOLOGY },
                    label = { Text("Technology") }
                )
            }
            item {
                // Creative Arts category chip
                FilterChip(
                    selected = selectedCategory == SkillCategory.CREATIVE_ARTS,
                    onClick = { selectedCategory = SkillCategory.CREATIVE_ARTS },
                    label = { Text("Creative Arts") }
                )
            }
            item {
                // Fitness & Lifestyle category chip
                FilterChip(
                    selected = selectedCategory == SkillCategory.FITNESS_LIFESTYLE,
                    onClick = { selectedCategory = SkillCategory.FITNESS_LIFESTYLE },
                    label = { Text("Fitness") }
                )
            }
            item {
                // Life Skills category chip
                FilterChip(
                    selected = selectedCategory == SkillCategory.LIFE_SKILLS,
                    onClick = { selectedCategory = SkillCategory.LIFE_SKILLS },
                    label = { Text("Life Skills") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skills List
        if (filteredSkills.isEmpty()) {
            // Show message when no skills match the filters
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center 
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedCategory != null) {
                        "No skills found" // When filters are active
                    } else {
                        "No skills available" // When no filters and no skills
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Display filtered skills list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSkills) { skill -> // 
                    SkillCard(
                        skill = skill,
                        onClick = { onSkillClick(skill.id) } // go to detail screen
                    )
                }
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