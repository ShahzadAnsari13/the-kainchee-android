package com.thekainchee.user.presentation.booking.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.BookingRepository
import com.thekainchee.user.presentation.booking.model.BookingUiModel
import com.thekainchee.user.presentation.booking.model.CreateBookingParams
import com.thekainchee.user.presentation.booking.state.BookingDetailUiState
import com.thekainchee.user.presentation.booking.state.BookingEvent
import com.thekainchee.user.presentation.booking.state.CreateBookingState
import com.thekainchee.user.presentation.booking.state.MyBookingsUiState
import com.thekainchee.user.presentation.booking.state.SlotState
import com.thekainchee.user.presentation.booking.state.StaffState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class BookingViewModel @Inject constructor(val repository: BookingRepository) : ViewModel() {

    private val _staffState = MutableStateFlow<StaffState>(StaffState.Idle)
    val staffState: StateFlow<StaffState> = _staffState

    private val  _slotState = MutableStateFlow<SlotState> (SlotState.Idle)
    val slotState : StateFlow<SlotState> = _slotState

    private val _createBookingState = MutableStateFlow<CreateBookingState>(CreateBookingState.Idle)
    val createBookingState : StateFlow<CreateBookingState> = _createBookingState
    private val _bookingEvent =
        MutableSharedFlow<BookingEvent>()
    val bookingEvent =_bookingEvent.asSharedFlow()

    private val _bookingDetailState = MutableStateFlow<BookingDetailUiState>(BookingDetailUiState.Idle)
    val bookingDetailState : StateFlow<BookingDetailUiState> = _bookingDetailState

    private val  _myBookingsState = MutableStateFlow<MyBookingsUiState>(MyBookingsUiState.Idle)
    val myBookingsState : StateFlow<MyBookingsUiState> = _myBookingsState
    private var bookingsJob: Job? = null
    fun getParlourStaffs(parlourId : String){

        _staffState.value = StaffState.Loading
        viewModelScope.launch {
            val result = repository.getParlourStaffs(parlourId)
            if(result.isSuccess){
                val data = result.getOrNull().orEmpty()

                if (data.isNotEmpty()) {

                    _staffState.value = StaffState.Success(data)

                } else {

                    _staffState.value = StaffState.Empty
                }

            }else{
                _staffState.value = StaffState.Error(result.exceptionOrNull()?.message ?: "Failed to load staffs")
            }
        }

    }

    fun getStaffSlots(parlourId: String, staffId: String, date: String){
        _slotState.value = SlotState.Loading
        viewModelScope.launch {
            val result = repository.getStaffSlots(parlourId, staffId, date)
            if(result.isSuccess){
                val data = result.getOrNull().orEmpty()
                if (data.isNotEmpty()) {
                    _slotState.value = SlotState.Success(data)
                } else {
                    _slotState.value = SlotState.Empty
                }
            }else{
                _slotState.value = SlotState.Error(result.exceptionOrNull()?.message ?: "Failed to load slots")
            }
        }
    }


    fun createBooking(createBookingParams: CreateBookingParams){

        if(createBookingState.value is CreateBookingState.Success) return
        if(createBookingState.value is CreateBookingState.Loading) return
        _createBookingState.value = CreateBookingState.Loading
        viewModelScope.launch {
            val result = repository.createBooking(createBookingParams)
            if(result.isSuccess){
                val data = result.getOrNull()
                if (data != null) {
                    _createBookingState.value = CreateBookingState.Success(data)
                    _bookingEvent.emit(
                        BookingEvent.OpenPaymentSheet(data)
                    )
                } else {
                    _createBookingState.value = CreateBookingState.Error("Failed to create booking")
                }
            }
            else{
                _createBookingState.value = CreateBookingState.Error(result.exceptionOrNull()?.message ?: "Failed to create booking")
            }
        }

    }
    fun resetCreateBookingState() {
        _createBookingState.value = CreateBookingState.Idle
    }

    fun getBookingDetails(bookingId: String) {

        _bookingDetailState.value = BookingDetailUiState.Loading

        viewModelScope.launch {

            repository.getBookingDetails(bookingId)
                .onSuccess {
                    _bookingDetailState.value =
                        BookingDetailUiState.Success(it)
                }
                .onFailure {
                    _bookingDetailState.value =
                        BookingDetailUiState.Error(
                            it.message ?: "Failed to load booking details"
                        )
                }
        }
    }
    fun getMyBookings(status: String){
        Log.d("BOOKING", "Status = $status")
        _myBookingsState.value = MyBookingsUiState.Loading
        bookingsJob?.cancel()
        bookingsJob = viewModelScope.launch {

            repository.getMyBookings(status)
                .onSuccess { bookings ->
                    Log.d("BOOKING", "Size = ${bookings.size}")
                    if(bookings.isEmpty()){

                        _myBookingsState.value =
                            MyBookingsUiState.Empty

                    }else{

                        _myBookingsState.value =
                            MyBookingsUiState.Success(
                                bookings
                            )

                    }
                }
                .onFailure {

                    Log.e("BOOKING", it.toString())
                    if (it is CancellationException) return@onFailure
                    _myBookingsState.value =
                        MyBookingsUiState.Error(
                            it.message ?: "Failed to load bookings"
                        )

                }
        }
    }
}