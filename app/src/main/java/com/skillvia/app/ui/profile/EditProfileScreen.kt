package com.skillvia.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.User
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onProfileUpdated: () -> Unit // Callback to refresh profile after update
) {
    val authRepository = AuthRepository()
    val skillRepo = SkillRepo()
    val scope = rememberCoroutineScope()
    
    // State for user data
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Form state
    var name by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    // UI state
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Load current user data
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    currentUser = skillRepo.getUserById(userId)
                    // Populate form with current data
                    currentUser?.let { user ->
                        name = user.name
                        university = user.university
                        studentId = user.studentId
                        bio = user.bio ?: "" // Use empty string if bio is null
                    }
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading profile: ${e.message}"
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Edit Profile") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        if (isLoading) {
            // Loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error message
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving
                )
                
                // University field (read-only for now, since it's UNB-only)
                OutlinedTextField(
                    value = university,
                    onValueChange = { }, // Disabled - university is fixed
                    label = { Text("University") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false, // Disabled since it's UNB-only for MVP
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                // Student ID field
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving
                )
                
                // Bio field (multi-line)
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isSaving,
                    placeholder = { Text("Tell others about yourself, your experience, or what you're looking for...") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Save button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Name cannot be empty"
                            return@Button
                        }
                        if (studentId.isBlank()) {
                            errorMessage = "Student ID cannot be empty"
                            return@Button
                        }
                        
                        isSaving = true
                        errorMessage = ""
                        
                        scope.launch {
                            try {
                                val userId = authRepository.getCurrentUserId()
                                if (userId != null) {
                                    // Update user profile in Supabase
                                    val success = authRepository.updateUserProfile(
                                        userId = userId,
                                        name = name,
                                        university = university, // Keep current university
                                        studentId = studentId,
                                        bio = bio.ifBlank { null } // Save as null if empty
                                    )
                                    
                                    if (success) {
                                        showSuccessDialog = true
                                    } else {
                                        errorMessage = "Failed to update profile. Please try again."
                                        isSaving = false
                                    }
                                } else {
                                    errorMessage = "You must be logged in to update your profile."
                                    isSaving = false
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error updating profile: ${e.message}"
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Save Changes")
                }
            }
        }
    }
    
    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Profile Updated") },
            text = { Text("Your profile has been updated successfully.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onProfileUpdated() // Refresh profile screen
                        onBackClick() // Navigate back
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

