package com.thekainchee.user.presentation.dashboard.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
import com.thekainchee.user.presentation.dashboard.home.state.BookingState
import com.thekainchee.user.presentation.dashboard.home.state.ParlourState
import com.thekainchee.user.presentation.dashboard.home.state.TrendingServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class ParlourViewModel @Inject constructor(private val parlourRepository: ParlourRepository) : ViewModel(){
    private val _nearbyParlourState = MutableStateFlow<ParlourState>(ParlourState.Idle)
    val nearbyParlourState: StateFlow<ParlourState> = _nearbyParlourState
    private val _trendingParlourState = MutableStateFlow<ParlourState>(ParlourState.Idle)
    val trendingParlourState: StateFlow<ParlourState> = _trendingParlourState

    private val _trendingServiceState  = MutableStateFlow<TrendingServiceState>(TrendingServiceState.Idle)
    val trendingServiceState : StateFlow<TrendingServiceState> = _trendingServiceState

    private val _bookingState  = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState : StateFlow<BookingState> = _bookingState
    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private var nearbyCurrentPage = 1
    private var nearbyIsLoading = false
    private var nearbyIsLastPage = false
    private val nearbyCurrentList = mutableListOf<ParlourUI>()

    private var trendingParlourIsLoading = false
    private val trendingParlourCurrentList = mutableListOf<ParlourUI>()


    private var trendingServiceIsLoading = false

    private var trendingServiceCurrentList = mutableListOf<ServiceUI>()


    private var upComingBookingIsLoading = false

    private var upComingBookingCurrentList = mutableListOf<BookingUI>()
    fun setLocation(lat: Double, lng: Double) {
        currentLat = lat
        currentLng = lng
    }

    fun upcomingBookings() {
        if (upComingBookingIsLoading) return

        _bookingState.value = BookingState.Loading

        viewModelScope.launch {
            upComingBookingIsLoading = true

            try {
                val result = parlourRepository.getUpcomingBookings(limit = 10)

                if (result.isSuccess) {
                    val data = result.getOrNull().orEmpty()

                    upComingBookingCurrentList.clear()

                    if (data.isNotEmpty()) {
                        upComingBookingCurrentList.addAll(data)
                    }

                    _bookingState.value =
                        BookingState.Success(upComingBookingCurrentList.toList())

                } else {
                    _bookingState.value = BookingState.Error(
                        message = result.exceptionOrNull()?.message
                            ?: "Failed to load Upcoming Bookings"
                    )
                }

            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error")

            } finally {
                upComingBookingIsLoading = false
            }
        }
    }
    fun trendingServices(){
        val lat = currentLat ?: return
        val lng = currentLng ?: return

        if(trendingServiceIsLoading) return

        trendingServiceCurrentList.clear()

        _trendingServiceState.value = TrendingServiceState.Loading
        viewModelScope.launch {
            trendingServiceIsLoading = true
            val result = parlourRepository.getTrendingServices(lat,lng)
            if(result.isSuccess){
                val data = result.getOrNull() ?: emptyList()
                trendingServiceIsLoading = false

                if (data.isNotEmpty()) {
                    trendingServiceCurrentList.addAll(data)
                }
                _trendingServiceState.value = TrendingServiceState.Success(trendingServiceCurrentList.toList())
            }else{
                trendingServiceIsLoading = false
                _trendingServiceState.value = TrendingServiceState.Error(message  = result.exceptionOrNull()?.message ?: "Failed to load Trending Service ")
            }
        }
    }

    fun trendingParlours(type: String?){
        val lat = currentLat ?: return
        val lng = currentLng ?: return


        if(trendingParlourIsLoading) return

        trendingParlourCurrentList.clear()
        _trendingParlourState.value = ParlourState.Loading
        viewModelScope.launch {
            trendingParlourIsLoading = true
            val result = parlourRepository.getTrendingParlours(lat=lat,lng=lng,type=type)

            if(result.isSuccess){
                val data = result.getOrNull() ?: emptyList()
                trendingParlourIsLoading = false

                if (data.isNotEmpty()) {
                    trendingParlourCurrentList.addAll(data)
                }
                _trendingParlourState.value = ParlourState.Success(trendingParlourCurrentList.toList(),false)
            }else{
                trendingParlourIsLoading = false
                _trendingParlourState.value = ParlourState.Error(message  = result.exceptionOrNull()?.message ?: "Failed to load parlours")
            }
        }
    }
    fun getNearbyParlours(
        type: String?,
        forceRefresh: Boolean = false
    ) {
        val lat = currentLat ?: return
        val lng = currentLng ?: return

        if (nearbyIsLoading) return

        if (forceRefresh) {
            nearbyCurrentPage = 1
            nearbyIsLastPage = false
        }

        if (nearbyCurrentPage == 1) {
            nearbyCurrentList.clear()
            _nearbyParlourState.value = ParlourState.Loading
        }

        viewModelScope.launch {

            nearbyIsLoading = true

            val result = parlourRepository.getNearbyParlours(
                lat, lng, nearbyCurrentPage, type
            )
            Log.d("RESULT_CHECK", result.toString())

            nearbyIsLoading = false

            if (result.isSuccess) {

                val data = result.getOrNull() ?: emptyList()

                if (data.isNotEmpty()) {
                    nearbyCurrentList.addAll(data)
                } else {
                    nearbyIsLastPage = true
                }

                _nearbyParlourState.value = ParlourState.Success(
                    nearbyCurrentList.toList(),
                    isPagination = false
                )

            } else {

                _nearbyParlourState.value = ParlourState.Error(
                    result.exceptionOrNull()?.message ?: "Failed"
                )
            }
        }
    }

    fun nearbyLoadNextPage(
        type: String?
    ) {
        val lat = currentLat ?: return
        val lng = currentLng ?: return
        if (nearbyIsLoading || nearbyIsLastPage) return

        viewModelScope.launch {

            nearbyIsLoading = true
            nearbyCurrentPage++

            val result = parlourRepository.getNearbyParlours(
                lat = lat,
                lng = lng,
                page = nearbyCurrentPage,
                type = type
            )

            if (result.isSuccess) {

                val data = result.getOrNull() ?: emptyList()

                nearbyIsLoading = false

                if (data.isNotEmpty()) {
                    nearbyCurrentList.addAll(data)
                } else {
                    nearbyIsLastPage = true
                }

                _nearbyParlourState.value = ParlourState.Success(
                    data = nearbyCurrentList.toList(),
                    isPagination = true
                )

            } else {

                nearbyIsLoading = false

                _nearbyParlourState.value = ParlourState.Error(
                    message = result.exceptionOrNull()?.message ?: "Pagination failed"
                )
            }
        }
    }

}