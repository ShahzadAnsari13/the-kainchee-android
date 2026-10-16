package com.alenear.user.data.repository

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.alenear.user.domain.repository.PlaceSearchRepository
import com.alenear.user.presentation.location.model.PlaceSuggestion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class PlaceSearchRepositoryImpl @Inject constructor( @ApplicationContext private val context: Context): PlaceSearchRepository {
    private val placesClient: PlacesClient by lazy {
        Places.createClient(context)
    }
    override suspend fun searchLocation(query: String): List<PlaceSuggestion> {
        return suspendCancellableCoroutine { continuation ->

            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setCountries(listOf("IN"))
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->

                    val results = response.autocompletePredictions
                        .take(5)
                        .map {
                            PlaceSuggestion(
                                placeId = it.placeId,
                                primaryText = it.getPrimaryText(null).toString(),
                                secondaryText = it.getSecondaryText(null).toString()
                            )
                        }

                    if (continuation.isActive) {
                        continuation.resume(results)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(emptyList())
                    }
                }
        }
    }

}