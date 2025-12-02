package com.skillvia.app.ui.skills

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.User
import com.skillvia.app.data.model.Review
import com.skillvia.app.data.repository.SkillRepo
import com.skillvia.app.ui.common.rememberUserLocationState
import com.skillvia.app.ui.components.RatingDialog
import com.skillvia.app.ui.theme.SkillviaCardDefaults
import com.skillvia.app.utils.LocationUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    skillId: String,
    onRequestSkill: (String) -> Unit, //unit represents void return type
    onBackClick: () -> Unit,
    onViewMap: (String, Double?, Double?) -> Unit = { _, _, _ -> } // Callback to view map
) {
    val skillRepo = SkillRepo()
    val userLocationState = rememberUserLocationState()
    var skill by remember { mutableStateOf<Skill?>(null) }
    var provider by remember { mutableStateOf<User?>(null) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(skillId) {
        scope.launch {
            skill = skillRepo.getSkillById(skillId)
            if (skill != null) {
                provider = skillRepo.getUserById(skill!!.providerID)
                reviews = skillRepo.getReviewsForSkill(skillId)
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
                        text = "Skill Details",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = skill!!.title, //non-null assertion
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = skill!!.category.replace("_", " "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "$${String.format("%.0f", skill!!.price)}/hr",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val reviewCount = if (reviews.isNotEmpty()) reviews.size else skill!!.totalRatings
                    val averageRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average().toFloat()
                    } else {
                        skill!!.rating
                    }
                    Text(
                        text = "${String.format("%.1f", averageRating)} ($reviewCount ${if (reviewCount == 1) "review" else "reviews"})",
                        style = MaterialTheme.typography.bodyLarge,
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
                                text = "Provider",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = provider?.name ?: "Loading...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = provider?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Display bio if available
                        if (!provider?.bio.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "About me:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = provider?.bio ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val deliveryTypeValue = (skill!!.deliveryType?.trim()?.takeIf { it.isNotEmpty() } ?: "BOTH").uppercase()
                val isLocationClickable = deliveryTypeValue != "ONLINE" && skill!!.location.isNotEmpty() && skillLatitude != null && skillLongitude != null
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isLocationClickable) {
                                Modifier.clickable {
                                    onViewMap(skill!!.location, skill!!.latitude?.toDouble(), skill!!.longitude?.toDouble())
                                }
                            } else {
                                Modifier
                            }
                        ),
                    shape = SkillviaCardDefaults.Shape,
                    elevation = SkillviaCardDefaults.elevation(),
                    colors = SkillviaCardDefaults.colors()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (isLocationClickable) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Tap to view on map",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        when (deliveryTypeValue) {
                            "ONLINE" -> {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = "Online",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            "IN_PERSON" -> {
                                Text(
                                    text = skill!!.location,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            "BOTH" -> {
                                Text(
                                    text = skill!!.location,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Also available online",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            else -> {
                                Text(
                                    text = skill!!.location,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        if (deliveryTypeValue != "ONLINE") {
                            Spacer(modifier = Modifier.height(12.dp))
                            when {
                                distanceText != null -> {
                                    AssistChip(
                                        onClick = { onViewMap(skill!!.location, skillLatitude, skillLongitude) },
                                        label = {
                                            Text(text = "$distanceText away")
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                }
                                !userLocationState.hasPermission -> {
                                    TextButton(
                                        onClick = userLocationState.requestPermission
                                    ) {
                                        Text("Enable location to see distance")
                                    }
                                }
                                userLocationState.isLoading -> {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp)
                                    )
                                }
                                skillLatitude == null || skillLongitude == null -> {
                                    Text(
                                        text = "Provider hasn't shared map coordinates yet.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = skill!!.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (reviews.isNotEmpty()) {
                    Text(
                        text = "Reviews (${reviews.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    reviews.forEach { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = SkillviaCardDefaults.Shape,
                            elevation = SkillviaCardDefaults.elevation(),
                            colors = SkillviaCardDefaults.colors()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = review.requesterName ?: "Anonymous",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(review.rating) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Star",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        repeat(5 - review.rating) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Empty star",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        }
                                    }
                                }
                                
                                if (!review.comment.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = review.comment,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                if (!review.createdAt.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatDate(review.createdAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (skill!!.totalRatings > 0) {
                    Text(
                        text = "No reviews with comments yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { onRequestSkill(skill!!.id)},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Request This Skill",
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
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Loading skill details...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val cleanDate = dateString.substringBefore(".").substringBefore("+").substringBefore("Z")
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(cleanDate)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        try {
            dateString.substringBefore("T")
        } catch (e2: Exception) {
            dateString
        }
    }
}

