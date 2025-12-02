package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import com.skillvia.app.ui.common.rememberUserLocationState
import com.skillvia.app.ui.theme.SkillviaCardDefaults
import com.skillvia.app.utils.LocationUtils
import kotlinx.coroutines.launch

@Composable
fun AddSkillRequestScreen(
    skillId: String,
    onRequestSubmitted: () -> Unit,
    onBackClick: () -> Unit
) {
   val skillRepo = SkillRepo()
   val authRepository = AuthRepository()
   val userLocationState = rememberUserLocationState()
   var skill by remember { mutableStateOf<Skill?>(null)}
   var provider by remember { mutableStateOf<User?>(null)}
   var requestMessage by remember { mutableStateOf("")}
   var deliveryPreference by remember { mutableStateOf<String?>(null) } // "ONLINE" or "IN_PERSON"
   var isLoading by remember { mutableStateOf(false)}
   var errorMessage by remember { mutableStateOf("")}
   var showSuccessDialog by remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()
   
   LaunchedEffect(skill) {
       skill?.let { s ->
           val deliveryType = (s.deliveryType?.trim() ?: "BOTH").uppercase()
           deliveryPreference = when (deliveryType) {
               "ONLINE" -> "ONLINE"
               "IN_PERSON" -> "IN_PERSON"
               else -> null // "BOTH" - user will choose
           }
       }
   }

   LaunchedEffect(skillId) {
    scope.launch {
        skill = skillRepo.getSkillById(skillId)
        if (skill != null) {
            provider = skillRepo.getUserById(skill!!.providerID)
        }
    }
   }

   Column(
    modifier = Modifier.fillMaxSize()
   ) {
    if (skill != null) {
        val skillLatitude = skill!!.latitude?.toDouble()
        val skillLongitude = skill!!.longitude?.toDouble()
        val userLat = userLocationState.latitude
        val userLng = userLocationState.longitude
        val distanceText = if (skillLatitude != null && skillLongitude != null && userLat != null && userLng != null) {
            val distanceKm = LocationUtils.calculateDistance(userLat, userLng, skillLatitude, skillLongitude)
            LocationUtils.formatDistance(distanceKm)
        } else null

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
                    text = "Request Skill",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = SkillviaCardDefaults.Shape,
                elevation = SkillviaCardDefaults.elevation(),
                colors = SkillviaCardDefaults.colors()
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

                    if (skill!!.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Location: ${skill!!.location}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (skillLatitude != null && skillLongitude != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        when {
                            distanceText != null -> {
                                Text(
                                    text = "Distance from you: $distanceText",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            !userLocationState.hasPermission -> {
                                TextButton(onClick = userLocationState.requestPermission) {
                                    Text("Enable location to see distance")
                                }
                            }
                            userLocationState.isLoading -> {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> {
                                Text(
                                    text = "Distance unavailable.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
            
            skill?.let { s ->
                val deliveryType = (s.deliveryType?.trim() ?: "BOTH").uppercase()
                if (deliveryType == "BOTH") {
                    Text(
                        text = "Delivery Preference",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = deliveryPreference == "ONLINE",
                                onClick = { deliveryPreference = "ONLINE" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = deliveryPreference == "IN_PERSON",
                                onClick = { deliveryPreference = "IN_PERSON" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "In-Person",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        
                        val userId = authRepository.getCurrentUserId()
                        
                        if (userId == null) {
                            errorMessage = "You must be logged in to request a skill"
                            isLoading = false
                            return@launch
                        }
                        
                        val request = SkillRequest(
                            skillId = skillId,
                            requesterId = userId,
                            providerId = skill!!.providerID,
                            message = requestMessage,
                            status = "PENDING",
                            price = skill!!.price,
                            deliveryPreference = deliveryPreference
                        )
                        
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
