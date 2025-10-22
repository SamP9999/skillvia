package com.skillvia.app.data.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    private const val SUPABASE_URL = "https://cikcgnwglgsryxxuklqe.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNpa2NnbndnbGdzcnl4eHVrbHFlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA3ODA5NDIsImV4cCI6MjA3NjM1Njk0Mn0.bQShgroSE3HTyMs_xos7Q3sMD3ZBulfREgQl9ZtvBU8"
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth) 
        install(Postgrest)
    }
}

