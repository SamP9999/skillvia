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
import java.util.Date
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
fun AddSkillScreen(
  onSkillAdded: () -> Unit,
  onBackClick: () -> Unit
) {
  val skillRepo = SkillRepo()
  val authRepository = AuthRepository()
  val context = LocalContext.current
  
  LaunchedEffect(Unit) {
    if (!Places.isInitialized()) {
      Places.initialize(context, "AIzaSyD8geDxm1ACba5CkrLHyRJYZKmCskTRrrI")
    }
  }
  
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("")}
  var price by remember { mutableStateOf("")}
  var location by remember { mutableStateOf("")}
  var latitude by remember { mutableStateOf<Double?>(null) }
  var longitude by remember { mutableStateOf<Double?>(null) }
  var selectedCategory by remember { mutableStateOf(SkillCategory.ACADEMIC)}
  var deliveryType by remember { mutableStateOf("BOTH") }
  
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

  var isLoading by remember { mutableStateOf(false)}
  var errorMessage by remember { mutableStateOf("")}
  var showSuccessDialog by remember { mutableStateOf(false)}

  val scope = rememberCoroutineScope()

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
          text = "Add New Skill",
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
        onValueChange = { title = it},
        placeholder = { Text("e.g., Math Tutoring, Guitar Lessons") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )
      Text(
        text = "Category",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )  
      var expanded by remember { mutableStateOf(false)}
      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
      ) {
        OutlinedTextField(
          value = selectedCategory.name.replace("_", " "),
          onValueChange = { }, //read only
          readOnly = true, //user can't edit the value
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
          modifier = Modifier
          .fillMaxWidth()
          .menuAnchor(),
        )
        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          // create items for each category
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
    Text(
      text = "Price per Hour ($)",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    OutlinedTextField(
      value = price,
      onValueChange = { price = it},
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
      Text(
        text = "Location",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))
      
      OutlinedTextField(
        value = location.ifEmpty { "Tap to select location" },
        onValueChange = { }, // Read-only - user must use Places Autocomplete
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
    Text(
      text = "Description",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    OutlinedTextField(
      value = description,
      onValueChange = { description = it},
      placeholder = { Text("Describe what you can offer...")},
      modifier = Modifier
      .fillMaxWidth()
      .height(120.dp),
      maxLines = 5,
      minLines = 3,
    )
   
    if (errorMessage.isNotEmpty()) {
      Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
      )
    }

    Button(
      onClick = {
        if(title.isBlank()){
          errorMessage = "Please enter a skill title"
          return@Button
        }
        if(description.isBlank()){
          errorMessage = "Please enter a description"
          return@Button
        }
        if(price.isBlank()){
          errorMessage = "Please enter a price"
          return@Button
        }
        if(deliveryType != "ONLINE" && location.isBlank()){
          errorMessage = "Please enter a location"
          return@Button
        }

        val priceValue = try {
          price.toDouble()
        } catch (e: NumberFormatException) {
          errorMessage = "Please enter a valid price"
          return@Button
        }
        scope.launch {
          isLoading = true
          errorMessage = ""

          try {
            val userId = authRepository.getCurrentUserId()
            if(userId == null){
              errorMessage = "You must be logged in to add a skill"
              isLoading = false
              return@launch
            }
            val newSkill = Skill(
              title = title.trim(),
              description = description.trim(),
              category = selectedCategory.name, // Convert enum to string
              price = priceValue,
              providerID  = userId,
              location = if (deliveryType == "ONLINE") "Online" else location.trim(),
              latitude = if (deliveryType == "ONLINE") null else latitude?.toFloat(),
              longitude = if (deliveryType == "ONLINE") null else longitude?.toFloat(),
              deliveryType = deliveryType,  
              rating = 0.0f,
              totalRatings = 0,
              isActive = true,
              createdAt = Date().toString()
            )
            
            val result = skillRepo.addSkill(newSkill)
            if(result.isSuccess){
              title = ""
              description = ""
              price = ""
              location = ""
              latitude = null
              longitude = null
              selectedCategory = SkillCategory.ACADEMIC
              deliveryType = "BOTH"
              showSuccessDialog = true
            } else {
              errorMessage = result.exceptionOrNull()?.message ?: "Failed to add skill"
            }
          }
          catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
          } finally {
            isLoading = false
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
      enabled = !isLoading,
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = MaterialTheme.colorScheme.onPrimary
        )
      } else {
        Text(
          text = "Add Skill",
          style = MaterialTheme.typography.titleMedium,
        )
      }
    }
    
    if(showSuccessDialog) {
      AlertDialog(
        onDismissRequest = { showSuccessDialog = false },
        title = { Text("Skill Added!") },
        text = { Text("Your skill has been added successfully and is now visible to other users.") }, // should add a review by admin before published for saferty reasons at some point
        confirmButton = {
          Button(
            onClick = {
              showSuccessDialog = false
              onSkillAdded() // go back to profile screen
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
