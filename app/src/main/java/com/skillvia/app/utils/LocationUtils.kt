package com.skillvia.app.utils

import kotlin.math.*

object LocationUtils {

    // Convert degrees to radians
    private fun toRadians(degrees: Double): Double {
        return degrees * PI / 180.0
    }
    // calculate distance between two points
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun formatDistance(distance: Double): String {
        return if (distance < 1.0) {
            "${(distance * 1000).toInt()}m"
        } else {
            "${"%.1f".format(distance)}km"
        }
    }
}