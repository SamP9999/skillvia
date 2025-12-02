package com.skillvia.app.ui.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

data class UserLocationState(
    val hasPermission: Boolean,
    val isLoading: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val requestPermission: () -> Unit,
    val refreshLocation: () -> Unit
)

@Composable
fun rememberUserLocationState(): UserLocationState {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        if (!hasPermission) return
        isLoading = true
        val cancellationTokenSource = CancellationTokenSource()
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                latitude = location?.latitude
                longitude = location?.longitude
                isLoading = false
            }
            .addOnFailureListener {
                // Fallback to lastLocation if getCurrentLocation fails
                fusedClient.lastLocation
                    .addOnSuccessListener { location ->
                        latitude = location?.latitude
                        longitude = location?.longitude
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            fetchLocation()
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        hasPermission = granted
        if (granted) {
            fetchLocation()
        }
    }

    return UserLocationState(
        hasPermission = hasPermission,
        isLoading = isLoading,
        latitude = latitude,
        longitude = longitude,
        requestPermission = {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        },
        refreshLocation = {
            if (hasPermission) {
                fetchLocation()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    )
}

