package com.skillvia.app.ui.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.RequestWithDetails
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import com.skillvia.app.ui.theme.SkillviaCardDefaults
import kotlinx.coroutines.launch

@Composable
fun RequestCard(
  requestWithDetails: RequestWithDetails,
  onAccept: (String) -> Unit,
  onReject: (String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = SkillviaCardDefaults.Shape,
    elevation = SkillviaCardDefaults.elevation(),
    colors = SkillviaCardDefaults.colors()
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
       OutlinedButton(
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
  
  var incomingRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var acceptedRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var completedRequests by remember { mutableStateOf<List<RequestWithDetails>>(emptyList())}
  var isLoading by remember { mutableStateOf(true)}
  var errorMessage by remember { mutableStateOf("")}
  val scope = rememberCoroutineScope()
  
  var showAcceptDialog by remember { mutableStateOf(false) }
  var showRejectDialog by remember { mutableStateOf(false) }
  var showCompleteDialog by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }
  var requestToHandle by remember { mutableStateOf<RequestWithDetails?>(null) }
  var successMessage by remember { mutableStateOf("") }
  var selectedTabIndex by remember { mutableStateOf(0) }

  LaunchedEffect(Unit) {
    scope.launch {
        try {
          val currentUserId = authRepository.getCurrentUserId()
          if ( currentUserId == null) {
            errorMessage = "You must be logged in to view requests"
            isLoading = false
            return@launch
          }
          
          val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
          
          incomingRequests = allRequests.filter { it.status == "PENDING" }
          acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
          completedRequests = allRequests.filter { it.status == "COMPLETED" }
          
          isLoading = false
        } catch (e: Exception){
          errorMessage = "Error loading requests: ${e.message}"
          isLoading = false
        }
    }
  }

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
            
            showAcceptDialog = false
            val tempRequest = requestToHandle
            requestToHandle = null
            
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "ACCEPTED")
              if (success) {
                successMessage = "Request accepted! You can now coordinate with $studentName."
                showSuccessDialog = true
                
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
                  incomingRequests = allRequests.filter { it.status == "PENDING" }
                  acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
                  completedRequests = allRequests.filter { it.status == "COMPLETED" }
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
            val requestId = requestToHandle?.id ?: ""
            val studentName = requestToHandle?.users?.name ?: "The student"
            
            showRejectDialog = false
            val tempRequest = requestToHandle
            requestToHandle = null
            
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "REJECTED")
              if (success) {
                successMessage = "Request rejected. $studentName has been notified."
                showSuccessDialog = true
                
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
                  incomingRequests = allRequests.filter { it.status == "PENDING" }
                  acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
                  completedRequests = allRequests.filter { it.status == "COMPLETED" }
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

  if (showCompleteDialog && requestToHandle != null) {
    AlertDialog(
      onDismissRequest = { 
        showCompleteDialog = false
        requestToHandle = null
      },
      title = { Text("Mark as Complete") },
      text = { 
        Text("Mark this request from ${requestToHandle?.users?.name ?: "this student"} as completed?\n\nThis indicates that you have finished providing the skill service.") 
      },
      confirmButton = {
        Button(
          onClick = {
            val requestId = requestToHandle?.id ?: ""
            val studentName = requestToHandle?.users?.name ?: "the student"
            
            showCompleteDialog = false
            requestToHandle = null
            
            scope.launch {
              val success = skillRepo.updateRequestStatus(requestId, "COMPLETED")
              if (success) {
                successMessage = "Request marked as completed! Great work with $studentName."
                showSuccessDialog = true
                
                val currentUserId = authRepository.getCurrentUserId()
                if(currentUserId != null) {
                  val allRequests = skillRepo.getAllRequestsForProvider(currentUserId)
                  incomingRequests = allRequests.filter { it.status == "PENDING" }
                  acceptedRequests = allRequests.filter { it.status == "ACCEPTED" }
                  completedRequests = allRequests.filter { it.status == "COMPLETED" }
                }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
          )
        ) {
          Text("Yes, Mark Complete")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { 
            showCompleteDialog = false
            requestToHandle = null
          }
        ) {
          Text("Cancel")
        }
      }
    )
  }

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

  val activeCount = incomingRequests.size + acceptedRequests.size
  
  LaunchedEffect(activeCount, completedRequests.size) {
    if (activeCount == 0 && completedRequests.isNotEmpty()) {
      selectedTabIndex = 1
    } else if (selectedTabIndex == 1 && completedRequests.isEmpty()) {
      selectedTabIndex = 0
    }
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
  else if (incomingRequests.isEmpty() && acceptedRequests.isEmpty() && completedRequests.isEmpty()) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Header with back button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
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
      
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No requests yet",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      }
    }
  }
  else {
    val activeCount = incomingRequests.size + acceptedRequests.size
    
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 16.dp),
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
          text = "Manage Requests",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
      
      val tabTitles = listOf(
        "Active ($activeCount)",
        "Completed (${completedRequests.size})"
      )
      
      TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        tabTitles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
              )
            }
          )
        }
      }
      
      Spacer(modifier = Modifier.height(8.dp))
      
      when (selectedTabIndex) {
        0 -> {
          if (activeCount == 0) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No active requests yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                    onAccept = {
                      requestToHandle = requestWithDetails
                      showAcceptDialog = true
                    },
                    onReject = {
                      requestToHandle = requestWithDetails
                      showRejectDialog = true
                    }
                  )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
              }
              
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
                    requestWithDetails = requestWithDetails,
                    onMarkComplete = {
                      requestToHandle = requestWithDetails
                      showCompleteDialog = true
                    }
                  )
                }
              }
            }
          }
        }
        else -> {
          if (completedRequests.isEmpty()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No completed requests yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              item {
                Text(
                  text = "Completed Requests",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                  text = "Past completed services",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(bottom = 8.dp)
                )
              }
              
              items(completedRequests) { requestWithDetails ->
                CompletedRequestCard(
                  requestWithDetails = requestWithDetails
                )
              }
            }
          }
        }
      }
    }
  }
}
}

@Composable
fun AcceptedRequestCard(
  requestWithDetails: RequestWithDetails,
  onMarkComplete: (String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = SkillviaCardDefaults.Shape,
    elevation = SkillviaCardDefaults.elevation(),
    colors = SkillviaCardDefaults.colors()
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
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = requestWithDetails.users?.name ?: "Unknown User",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
          )
        }
        Text(
          text = "$${String.format("%.0f", requestWithDetails.price)}/hr",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
      
      Text(
        text = "Message:",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = requestWithDetails.message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Status badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Check,
          contentDescription = "Accepted",
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "ACCEPTED",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        }
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      
      Button(
        onClick = { onMarkComplete(requestWithDetails.id ?: "") },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.tertiary
        )
      ) {
        Icon(
          Icons.Default.Done,
          contentDescription = "Complete",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Mark as Complete")
      }
      
      Spacer(modifier = Modifier.height(8.dp))
      
      Text(
        text = "💡 Coordinate meeting details with the student through app messaging (coming soon) or contact them directly.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
      )
    }
  }
}

@Composable
fun CompletedRequestCard(
  requestWithDetails: RequestWithDetails
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = SkillviaCardDefaults.Shape,
    elevation = SkillviaCardDefaults.elevation(),
    colors = SkillviaCardDefaults.colors()
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
          color = MaterialTheme.colorScheme.tertiary,
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
      
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
      
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Done,
          contentDescription = "Completed",
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "COMPLETED",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.tertiary
        )
      }
    }
  }
}
