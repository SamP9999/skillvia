package com.skillvia.app.data.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL_HERE"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_KEY_HERE"
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth) 
        install(Postgrest)
    }
}

