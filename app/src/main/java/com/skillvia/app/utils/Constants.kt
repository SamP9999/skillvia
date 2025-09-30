package com.skillvia.app.utils

object Constants {
    // App Configuration
    const val APP_NAME = "Skillvia"
    const val APP_VERSION = "1.0.0"

    // add Supabase Configuration (add your actual values later)
    //make sure i don't put anon key in this file so it doesn't get commited to git

    // Price Limits
    const val MIN_PRICE = 0.0
    const val MAX_PRICE = 100.0
    const val DEFAULT_PRICE = 15.0

    // Location Settings
    const val MAX_DISTANCE = 50.0 // km
    const val DEFAULT_RADIUS = 10.0 // km
    const val LOCATION_UPDATE_INTERVAL = 30000L // 30 seconds

    // Rating System
    const val MIN_RATING = 1
    const val MAX_RATING = 5
    const val DEFAULT_RATING = 0.0f

    // Request Timeouts
    const val REQUEST_TIMEOUT = 14 * 24 * 60 * 60 * 1000L // 14 days - how long skill request stays active before cancelled
    const val RESPONSE_TIMEOUT = 24 * 60 * 60 * 1000L // user has 24 hours to respond to skill request before it expires

    // University Settings
    const val UNIVERSITY_NAME = "University of New Brunswick"
    const val UNIVERSITY_DOMAIN = "unb.ca"

    // UI Constants

}
