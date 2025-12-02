package com.skillvia.app.data.repository

import com.skillvia.app.data.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {
    private val supabase = SupabaseClient.client

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
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
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
    
    suspend fun updateUserProfile(
        userId: String,
        name: String,
        university: String,
        studentId: String,
        bio: String? = null
    ): Boolean {
        return try {
            supabase.from("users")
                .update(
                    {
                        set("name", name)
                        set("university", university)
                        set("student_id", studentId)
                        set("bio", bio as String?)
                    }
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

