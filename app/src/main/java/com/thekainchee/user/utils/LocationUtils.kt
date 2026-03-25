package com.thekainchee.user.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

object LocationUtils {
    fun checkGpsStatus(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onGpsReady: () -> Unit
    ) {

        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)

        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            onGpsReady()
        }

        task.addOnFailureListener { exception ->

            if (exception is ResolvableApiException) {

                val intentSenderRequest =
                    IntentSenderRequest.Builder(exception.resolution).build()

                launcher.launch(intentSenderRequest)
            }else {
                activity.finishAffinity()
            }
        }
    }


    fun checkLocationPermission(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>,
        onPermissionGranted: () -> Unit
    ) {

        val fineLocation =
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation =
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocation || coarseLocation) {

            onPermissionGranted()

        } else {

            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


}