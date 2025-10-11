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
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch

@Composable
fun SkillsListScreen(
    onSkillClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val skillRepo = SkillRepo()  // Create repository instance
    var skills by remember { mutableStateOf<List<Skill>>(emptyList()) }  // State for skills list
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
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
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
}

@Composable
fun SkillCard(
    skill: Skill, 
    onClick: () -> Unit 
) {
    Card(  
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(), 
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)  // Add shadow
    ) {
        Column(  // Vertical layout inside card
            modifier = Modifier.padding(16.dp) 
        ) {
            Row(  // Horizontal layout for title and price
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,  
                verticalAlignment = Alignment.Top  
            ) {
                Column(modifier = Modifier.weight(1f)) {  // Left side takes remaining space
                    Text(  // Skill title
                        text = skill.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(  // Provider name
                        text = skill.providerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(  // Price on right side
                    text = "$${String.format("%.0f", skill.price)}/hr",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))  // Add space
            
            Text(  // Description
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,  // Show only 2 lines
                overflow = TextOverflow.Ellipsis  // Add "..." if too long
            )
            
            Spacer(modifier = Modifier.height(8.dp))  // Add space
            
            Row(  // Bottom row for rating and location
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically  
            ) {
                Text(  // Rating
                    text = "★ ${String.format("%.1f", skill.rating)} (${skill.totalRatings})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(  // Location
                    text = skill.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}