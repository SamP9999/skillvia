package com.skillvia.app.ui.skills

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
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillCategory
import com.skillvia.app.data.repository.AuthRepository
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkillScreen(
  onSkillAdded: () -> Unit,
  onBackClick: () -> Unit
) {
  val skillRepo = SkillRepo()
  val authRepository = AuthRepository()
//form fields
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("")}
  var price by remember { mutableStateOf("")}
  var location by remember { mutableStateOf("")}
  var selectedCategory by remember { mutableStateOf(SkillCategory.ACADEMIC)}

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
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
          text = "Add New Skill",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
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
      //Dropdown for categories
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
    //price
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
    //location
    Text(
      text = "Location",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    OutlinedTextField(
      value = location,
      onValueChange = { location = it},
      placeholder = { Text("e.g., Harriet Irving Library") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
    )
    //description
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
   
    //error message
    if (errorMessage.isNotEmpty()) {
      Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
      )
    }

    //submit button
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
        if(location.isBlank()){
          errorMessage = "Please enter a location"
          return@Button
        }

        // Make sure price is a valid number
        val priceValue = try {
          price.toDouble()
        } catch (e: NumberFormatException) {
          errorMessage = "Please enter a valid price"
          return@Button
        }
        //adding skill to supabase
        scope.launch {
          isLoading = true
          errorMessage = ""

          try {
            val userId = authRepository.getCurrentUserId()
            if(userId == null){
              errorMessage = "You must be logged in to add a skill"
              isLoading = false
              return@launch //stop execution if user not logged in
            }
            //skill object 
            val newSkill = Skill(
              title = title.trim(),
              description = description.trim(),
              category = selectedCategory.name, // Convert enum to string
              price = priceValue,
              providerID  = userId,
              location = location.trim(),
              rating = 0.0f,
              totalRatings = 0,
              isActive = true,
              createdAt = Date().toString()
            )
            //save to supabase
            val result = skillRepo.addSkill(newSkill)
            if(result.isSuccess){
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
