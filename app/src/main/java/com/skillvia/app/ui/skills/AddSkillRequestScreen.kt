package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillRequest
import com.skillvia.app.data.model.RequestStatus
import com.skillvia.app.data.model.User
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkillRequestScreen(
    skillId: String,
    onRequestSubmitted: () -> Unit,
    onBackClick: () -> Unit
) {
   val skillRepo = SkillRepo()
   val authRepository = AuthRepository()
   var skill by remember { mutableStateOf<Skill?>(null)}
   var provider by remember { mutableStateOf<User?>(null)}
   var requestMessage by remember { mutableStateOf("")}
   var isLoading by remember { mutableStateOf(false)}
   var errorMessage by remember { mutableStateOf("")}
   var showSuccessDialog by remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()

   LaunchedEffect(skillId) { //load skill data when screen opens
    scope.launch {
        skill = skillRepo.getSkillById(skillId)
        // Fetch provider information
        if (skill != null) {
            provider = skillRepo.getUserById(skill!!.providerID)
        }
    }
   }

   Column(
    modifier = Modifier.fillMaxSize()
   ) {
    TopAppBar(
        title = {Text("Request Skill")},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )

    if (skill != null) {
        Column(
            modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        ) {
            //skill summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Requesting: ${skill!!.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Provider",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = provider?.name ?: "Loading...",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = provider?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text( //price info
                        text = "Rate: $${String.format("%.0f", skill!!.price)}/hr",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Request Message Section
            Text(
                text = "Request Message",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Write a message to ${provider?.name ?: "the provider"} explaining what you need help with:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = requestMessage,
                onValueChange = { requestMessage = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Hi ${provider?.name ?: "there"}! I need help with...")
                },
                maxLines = 5,
                minLines = 3 // might change, test this later
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        
                        // Get current user ID
                        val userId = authRepository.getCurrentUserId()
                        
                        if (userId == null) {
                            errorMessage = "You must be logged in to request a skill"
                            isLoading = false
                            return@launch //stop execution if user not logged in
                        }
                        
                        // Create skill request
                        val request = SkillRequest(
                            skillId = skillId,
                            requesterId = userId,
                            providerId = skill!!.providerID,
                            message = requestMessage,
                            status = "PENDING",
                            price = skill!!.price
                        )
                        
                        // Save to Supabase
                        val result = skillRepo.createRequest(request)
                        isLoading = false
                        
                        if (result.isSuccess) {
                            showSuccessDialog = true
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Failed to submit request"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && requestMessage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary)
                } 
                else {
                    Text(
                        text = "Send Request",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    else { // show loading if skill not loaded yet
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Request Sent!") },
            text = { Text("Your skill request has been sent successfully. The provider will be notified.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onRequestSubmitted()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
  } 
}
