package com.skillvia.app.ui.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.RequestWithDetails
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch

@Composable
fun RequestCard(
  requestWithDetails: RequestWithDetails,
  onAccept: (String) -> Unit,
  onReject: (String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(
          modifier = Modifier.weight(1f),
        ) {
          Text(
            text = requestWithDetails.skills?.title ?: "Unknown Skill",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "Request from ${requestWithDetails.users?.name ?: "Unknown User"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
      }
      Text(
        text = "$${String.format("%.0f", requestWithDetails.price)}/hr",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )
    }

    Spacer(modifier = Modifier.height(12.dp))
    //request message section
    Text(
      text = "Message:",
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = requestWithDetails.message,
      style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(modifier = Modifier.height(16.dp))
    //action buttons section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {onAccept(requestWithDetails.id ?: "")},
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
        )
      ) {
        Icon(
          Icons.Default.Check,
          contentDescription = "Accept",
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Accept")
      }
       OutlinedButton( // Reject button
                    onClick = { onReject(requestWithDetails.id ?: "") }, 
                    modifier = Modifier.weight(1f) 
       ) { 
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Reject", 
                        modifier = Modifier.size(18.dp) 
                    )
                    Spacer(modifier = Modifier.width(4.dp)) 
                    Text("Reject") 
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestManagementScreen(
  onBackClick: () -> Unit
) {
  val skillRepo = SkillRepo()
  val authRepository = AuthRepository()
  var incomingRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var isLoading by remember { mutableStateOf(true)}
  var errorMessage by remember { mutableStateOf("")}
  val scope = rememberCoroutineScope()

  // Load data when screen first appears
  LaunchedEffect(Unit) {
    scope.launch {
        try {
          val currentUserId = authRepository.getCurrentUserId()
          if ( currentUserId == null) {
            errorMessage = "You must be logged in to view requests"
            isLoading = false
            return@launch // stop execution if user not logged in
          }
          // Fetch requests with skill titles and requester names
          val requestsWithDetails = skillRepo.getRequestsWithDetails(currentUserId)

          incomingRequests = requestsWithDetails
          isLoading = false
        } catch (e: Exception){
          errorMessage = "Error loading requests: ${e.message}"
          isLoading = false
        }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    TopAppBar(
      title = {Text("Manage Requests")},
      navigationIcon = {
        IconButton(onClick = onBackClick) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
      }
    )
    if (isLoading) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
    else if (errorMessage.isNotEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = errorMessage,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyLarge,
        )
      }
  }
  else if (incomingRequests.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No pending requests",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
  else { //show requests if there are any
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(incomingRequests) { requestWithDetails ->
        RequestCard(
          requestWithDetails = requestWithDetails,
          onAccept = {requestId ->
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "ACCEPTED")
              if (success) {
                //refresh list
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val updatedRequests = skillRepo.getRequestsWithDetails(currentUserId)
                  incomingRequests = updatedRequests
                }
              }
           }
          },
          onReject = {requestId ->
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "REJECTED")
              if (success) {
                //refresh list
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val updatedRequests = skillRepo.getRequestsWithDetails(currentUserId)
                  incomingRequests = updatedRequests
                }
              }
            }
          }
        )
      }
    }
  }
}
}
