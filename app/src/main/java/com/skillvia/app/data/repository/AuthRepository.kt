package com.skillvia.app.data.repository

import com.skillvia.app.data.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {
    private val supabase = SupabaseClient.client
    
    // TODO: PRODUCTION - Re-enable email confirmation in Supabase Dashboard
    // Currently disabled for demo/development purposes
    // Steps to re-enable:
    // 1. Go to: Supabase Dashboard > Authentication > Providers > Email
    // 2. Turn ON "Confirm email"
    // 3. Implement email verification UI flow
    // 4. Update signUp() to handle unverified users gracefully
    
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        university: String,
        studentId: String
    ): Result<String> {
        return try {
            // Sign up with Supabase Auth (creates user and auto-signs them in when email confirmation is OFF)
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            // Get user ID from session
            val userId = supabase.auth.currentUserOrNull()?.id 
                ?: throw Exception("User ID not found after signup")
            
            // Create user profile in users table
            supabase.from("users").insert(mapOf(
                "id" to userId,
                "email" to email,
                "name" to name,
                "university" to university,
                "student_id" to studentId
            ))

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(
        email: String,
        password: String
    ): Result<String> {
        return try {
            // Sign in with Supabase Auth
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            // Get the user ID
            val userId = supabase.auth.currentUserOrNull()?.id 
                ?: throw Exception("User ID not found after login")
            
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            // Handle sign out error, need to implement
        }
    }
    
    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }
    
    fun isLoggedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}

