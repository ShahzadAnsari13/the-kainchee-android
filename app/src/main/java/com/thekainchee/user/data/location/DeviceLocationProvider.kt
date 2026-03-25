package com.thekainchee.user.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DeviceLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { continuation ->

            val fineLocation =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            val coarseLocation =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (!fineLocation && !coarseLocation) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val priority =
                if (fineLocation)
                    Priority.PRIORITY_HIGH_ACCURACY
                else
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null && continuation.isActive) {
                        continuation.resume(location)
                    } else {

                        fusedLocationClient.getCurrentLocation(
                            priority,
                            null
                        ).addOnSuccessListener { location ->
                            if (continuation.isActive) {
                                continuation.resume(location)
                            }

                        }.addOnFailureListener {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
        }
}