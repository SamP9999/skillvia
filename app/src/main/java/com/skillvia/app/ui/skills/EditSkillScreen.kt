package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillCategory
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.content.Intent
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSkillScreen(
    skillId: String,
    onSkillUpdated: () -> Unit,
    onBackClick: () -> Unit
) {
    val skillRepo = SkillRepo()
    val authRepository = AuthRepository()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyD8geDxm1ACba5CkrLHyRJYZKmCskTRrrI")
        }
    }
    
    var skill by remember { mutableStateOf<Skill?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var selectedCategory by remember { mutableStateOf(SkillCategory.ACADEMIC) }
    var deliveryType by remember { mutableStateOf("BOTH") }
    
    LaunchedEffect(skillId) {
        isLoading = true
        try {
            val loadedSkill = skillRepo.getSkillById(skillId)
            if (loadedSkill != null) {
                skill = loadedSkill
                title = loadedSkill.title
                description = loadedSkill.description
                price = loadedSkill.price.toString()
                location = if (loadedSkill.deliveryType == "ONLINE") "" else loadedSkill.location
                latitude = loadedSkill.latitude?.toDouble()
                longitude = loadedSkill.longitude?.toDouble()
                deliveryType = loadedSkill.deliveryType?.trim()?.takeIf { it.isNotEmpty() } ?: "BOTH"
                
                try {
                    selectedCategory = SkillCategory.valueOf(loadedSkill.category)
                } catch (e: Exception) {
                    selectedCategory = SkillCategory.ACADEMIC
                }
            } else {
                errorMessage = "Skill not found"
            }
        } catch (e: Exception) {
            errorMessage = "Error loading skill: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    val placesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data ?: return@rememberLauncherForActivityResult)
            location = place.name ?: place.address ?: ""
            latitude = place.latLng?.latitude
            longitude = place.latLng?.longitude
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (skill == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage.ifEmpty { "Skill not found" },
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Go Back")
            }
        }
        return
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = "Edit Skill",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Skill Title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g., Math Tutoring, Guitar Lessons") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory.name.replace("_", " "),
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SkillCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name.replace("_", " ")) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Price per Hour ($)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("e.g., 15") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Text("$") },
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Delivery Type",
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
                        selected = deliveryType == "ONLINE",
                        onClick = { deliveryType = "ONLINE" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Online Only",
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
                        selected = deliveryType == "IN_PERSON",
                        onClick = { deliveryType = "IN_PERSON" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "In-Person Only",
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
                        selected = deliveryType == "BOTH",
                        onClick = { deliveryType = "BOTH" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Both Available",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            if (deliveryType != "ONLINE") {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = location.ifEmpty { "Tap to select location" },
                    onValueChange = { },
                    readOnly = true,
                    placeholder = { Text("Tap to select a location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
                    trailingIcon = {
                        if (location.isNotEmpty()) {
                            IconButton(onClick = {
                                location = ""
                                latitude = null
                                longitude = null
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(context)
                        placesLauncher.launch(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (location.isEmpty()) "Select Location" else "Change Location")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe what you can offer...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                minLines = 3,
            )
            
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter a skill title"
                        return@Button
                    }
                    if (description.isBlank()) {
                        errorMessage = "Please enter a description"
                        return@Button
                    }
                    if (price.isBlank()) {
                        errorMessage = "Please enter a price"
                        return@Button
                    }
                    if (deliveryType != "ONLINE" && location.isBlank()) {
                        errorMessage = "Please enter a location"
                        return@Button
                    }
                    
                    val priceValue = try {
                        price.toDouble()
                    } catch (e: NumberFormatException) {
                        errorMessage = "Please enter a valid price"
                        return@Button
                    }
                    
                    if (priceValue <= 0) {
                        errorMessage = "Price must be greater than 0"
                        return@Button
                    }
                    
                    scope.launch {
                        isSaving = true
                        errorMessage = ""
                        
                        try {
                            val userId = authRepository.getCurrentUserId()
                            if (userId == null) {
                                errorMessage = "You must be logged in to edit a skill"
                                isSaving = false
                                return@launch
                            }
                            
                            val updatedSkill = skill!!.copy(
                                title = title.trim(),
                                description = description.trim(),
                                category = selectedCategory.name,
                                price = priceValue,
                                location = if (deliveryType == "ONLINE") "Online" else location.trim(),
                                latitude = if (deliveryType == "ONLINE") null else latitude?.toFloat(),
                                longitude = if (deliveryType == "ONLINE") null else longitude?.toFloat(),
                                deliveryType = deliveryType
                            )
                            
                            val result = skillRepo.updateSkill(updatedSkill)
                            if (result.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to update skill"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Update Skill",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Skill Updated!") },
                    text = { Text("Your skill has been updated successfully.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onSkillUpdated()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

