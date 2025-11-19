package com.skillvia.app.ui.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
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

@Composable
fun RequestManagementScreen(
  onBackClick: () -> Unit
) {
  val skillRepo = SkillRepo()
  val authRepository = AuthRepository()
  
  // State for requests and UI
  var incomingRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var acceptedRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var isLoading by remember { mutableStateOf(true)}
  var errorMessage by remember { mutableStateOf("")}
  val scope = rememberCoroutineScope()
  
  // State for confirmation dialogs
  var showAcceptDialog by remember { mutableStateOf(false) }
  var showRejectDialog by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }
  var requestToHandle by remember { mutableStateOf<RequestWithDetails?>(null) }
  var successMessage by remember { mutableStateOf("") }

  // Load data when screen first appears
  LaunchedEffect(Unit) {
    scope.launch {
        try {
          val currentUserId = authRepository.getCurrentUserId()
          if ( currentUserId == null) {
            errorMessage = "You must be logged in to view requests"
            isLoading = false
            return@launch
          }
          
          // Fetch ALL requests for this provider (both pending and accepted)
          val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
          
          // Split into two lists based on status
          incomingRequests = allRequests.filter { it.status == "PENDING" }
          acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
          
          isLoading = false
        } catch (e: Exception){
          errorMessage = "Error loading requests: ${e.message}"
          isLoading = false
        }
    }
  }

  // Accept Confirmation Dialog
  if (showAcceptDialog && requestToHandle != null) {
    AlertDialog(
      onDismissRequest = { 
        showAcceptDialog = false
        requestToHandle = null
      },
      title = { Text("Accept Request") },
      text = { 
        Text("Are you sure you want to accept this request from ${requestToHandle?.users?.name ?: "this student"}?\n\nThey will be notified and you can coordinate meeting details.") 
      },
      confirmButton = {
        Button(
          onClick = {
            // Capture the request details before clearing state
            val requestId = requestToHandle?.id ?: ""
            val studentName = requestToHandle?.users?.name ?: "the student"
            
            // Close dialog and clear state first
            showAcceptDialog = false
            val tempRequest = requestToHandle
            requestToHandle = null
            
            // Then perform the async operation
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "ACCEPTED")
              if (success) {
                // Show success message
                successMessage = "Request accepted! You can now coordinate with $studentName."
                showSuccessDialog = true
                
                // Refresh BOTH lists
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
                  incomingRequests = allRequests.filter { it.status == "PENDING" }
                  acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
                }
              }
            }
          }
        ) {
          Text("Yes, Accept")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { 
            showAcceptDialog = false
            requestToHandle = null
          }
        ) {
          Text("Cancel")
        }
      }
    )
  }

  // Reject Confirmation Dialog
  if (showRejectDialog && requestToHandle != null) {
    AlertDialog(
      onDismissRequest = { 
        showRejectDialog = false
        requestToHandle = null
      },
      title = { Text("Reject Request") },
      text = { 
        Text("Are you sure you want to reject this request from ${requestToHandle?.users?.name ?: "this student"}?\n\nThey will be notified that you cannot fulfill this request.") 
      },
      confirmButton = {
        Button(
          onClick = {
            // Capture the request details before clearing state
            val requestId = requestToHandle?.id ?: ""
            val studentName = requestToHandle?.users?.name ?: "The student"
            
            // Close dialog and clear state first
            showRejectDialog = false
            val tempRequest = requestToHandle
            requestToHandle = null
            
            // Then perform the async operation
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "REJECTED")
              if (success) {
                // Show success message
                successMessage = "Request rejected. $studentName has been notified."
                showSuccessDialog = true
                
                // Refresh BOTH lists
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
                  incomingRequests = allRequests.filter { it.status == "PENDING" }
                  acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
                }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
          )
        ) {
          Text("Yes, Reject")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { 
            showRejectDialog = false
            requestToHandle = null
          }
        ) {
          Text("Cancel")
        }
      }
    )
  }

  // Success Dialog (shows after accept/reject)
  if (showSuccessDialog) {
    AlertDialog(
      onDismissRequest = { showSuccessDialog = false },
      title = { Text("Success") },
      text = { Text(successMessage) },
      confirmButton = {
        Button(onClick = { showSuccessDialog = false }) {
          Text("OK")
        }
      }
    )
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
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
  else if (incomingRequests.isEmpty() && acceptedRequests.isEmpty()) {
    // No requests at all
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No requests yet",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
  else {
    // Show both sections in a scrollable column
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
          Text(
            text = "Manage Requests",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
      
      // Section 1: Incoming Requests (Pending)
      if (incomingRequests.isNotEmpty()) {
        item {
          Text(
            text = "Incoming Requests",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
          )
        }
        
        items(incomingRequests) { requestWithDetails ->
          RequestCard(
            requestWithDetails = requestWithDetails,
            onAccept = { requestId ->
              requestToHandle = requestWithDetails
              showAcceptDialog = true
            },
            onReject = { requestId ->
              requestToHandle = requestWithDetails
              showRejectDialog = true
            }
          )
        }
        
        // Spacer between sections
        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
      
      // Section 2: Accepted Requests (Active)
      if (acceptedRequests.isNotEmpty()) {
        item {
          Text(
            text = "Accepted Requests",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
          )
          Text(
            text = "Coordinate with these students",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }
        
        items(acceptedRequests) { requestWithDetails ->
          AcceptedRequestCard(
            requestWithDetails = requestWithDetails
          )
        }
      }
    }
  }
}
}

// Card for accepted requests (no accept/reject buttons, shows status)
@Composable
fun AcceptedRequestCard(
  requestWithDetails: RequestWithDetails
) {
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
            text = requestWithDetails.skills?.title ?: "Unknown Skill",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = requestWithDetails.users?.name ?: "Unknown User",
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
      
      // Show the original message
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
        text = "💡 Coordinate meeting details with the student through app messaging (coming soon) or contact them directly.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
      )
    }
  }
}
