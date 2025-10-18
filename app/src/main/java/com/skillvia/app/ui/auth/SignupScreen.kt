package com.skillvia.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.skillvia.app.data.repository.AuthRepository

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authRepository = AuthRepository()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    
    // UNB-only for MVP (fixed value)
    val university = "University of New Brunswick"
    
    // TODO: For multi-university expansion, uncomment below and remove the fixed university above:
    // var selectedUniversity by remember { mutableStateOf("") }
    // val universities = listOf(
    //     "University of New Brunswick",
    //     "Dalhousie University",
    //     "Saint Mary's University",
    //     "Mount Allison University"
    // )
    // val universityToEmailDomain = mapOf(
    //     "University of New Brunswick" to "@unb.ca",
    //     "Dalhousie University" to "@dal.ca",
    //     "Saint Mary's University" to "@smu.ca",
    //     "Mount Allison University" to "@mta.ca"
    // )
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Join Skillvia to learn and teach",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("University Email") },
            placeholder = { Text("yourname@unb.ca") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            placeholder = { Text("e.g. 123456789") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // UNB-only: Fixed university field (disabled)
        OutlinedTextField(
            value = university,
            onValueChange = { },
            label = { Text("University") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = false
        )
        
        // TODO: For multi-university expansion, replace above OutlinedTextField with dropdown:
        // var expanded by remember { mutableStateOf(false) }
        // ExposedDropdownMenuBox(
        //     expanded = expanded,
        //     onExpandedChange = { expanded = !expanded }
        // ) {
        //     OutlinedTextField(
        //         value = selectedUniversity,
        //         onValueChange = { },
        //         readOnly = true,
        //         label = { Text("University") },
        //         trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        //         modifier = Modifier
        //             .fillMaxWidth()
        //             .menuAnchor(),
        //         enabled = !isLoading
        //     )
        //     ExposedDropdownMenu(
        //         expanded = expanded,
        //         onDismissRequest = { expanded = false }
        //     ) {
        //         universities.forEach { uni ->
        //             DropdownMenuItem(
        //                 text = { Text(uni) },
        //                 onClick = {
        //                     selectedUniversity = uni
        //                     expanded = false
        //                 }
        //             )
        //         }
        //     }
        // }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                // UNB-only validation
                when {
                    name.isBlank() -> errorMessage = "Please enter your name"
                    email.isBlank() -> errorMessage = "Please enter your email"
                    !email.endsWith("@unb.ca") -> errorMessage = "Must use UNB email (@unb.ca)"
                    studentId.isBlank() -> errorMessage = "Please enter your student ID"
                    studentId.length != 7 -> errorMessage = "UNB student ID must be 7 digits"
                    password.isBlank() -> errorMessage = "Please enter a password"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    password != confirmPassword -> errorMessage = "Passwords don't match"
                    else -> {
                      errorMessage = ""
                      isLoading = true
                      scope.launch {
                          val result = authRepository.signUp(email, password, name, university, studentId)
                          isLoading = false
                          if (result.isSuccess) {
                              onSignupSuccess()
                          } else {
                              errorMessage = result.exceptionOrNull()?.message ?: "Signup failed"
                          }
                      }
                    }
                  }
                
                // TODO: For multi-university expansion, replace above validation with:
                // when {
                //     name.isBlank() -> errorMessage = "Please enter your name"
                //     email.isBlank() -> errorMessage = "Please enter your email"
                //     selectedUniversity.isBlank() -> errorMessage = "Please select your university"
                //     else -> {
                //         val expectedDomain = universityToEmailDomain[selectedUniversity]
                //         when {
                //             expectedDomain == null -> errorMessage = "Invalid university selected"
                //             !email.endsWith(expectedDomain) -> errorMessage = "Email must match your university domain ($expectedDomain)"
                //             studentId.isBlank() -> errorMessage = "Please enter your student ID"
                //             password.isBlank() -> errorMessage = "Please enter a password"
                //             password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                //             password != confirmPassword -> errorMessage = "Passwords don't match"
                //             else -> {
                //                 errorMessage = ""
                //                 isLoading = true
                //                 scope.launch {
                //                     kotlinx.coroutines.delay(1500)
                //                     isLoading = false
                //                     onSignupSuccess()
                //                 }
                //             }
                //         }
                //     }
                // }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onNavigateToLogin,
            enabled = !isLoading
        ) {
            Text(
                text = "Already have an account? Login",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
