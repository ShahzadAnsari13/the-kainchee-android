package com.thekainchee.user.presentation.payment.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.data.remote.dto.payment.VerifyPaymentRequestDto
import com.thekainchee.user.domain.repository.PaymentRepository
import com.thekainchee.user.domain.repository.WalletRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.payment.model.VerifyPaymentParams
import com.thekainchee.user.presentation.payment.state.OnlinePaymentEvent
import com.thekainchee.user.presentation.payment.state.PaymentCallbackEvent
import com.thekainchee.user.presentation.payment.state.PaymentEvent
import com.thekainchee.user.presentation.payment.state.WalletBalanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: WalletRepository,private val paymentRepository: PaymentRepository) : ViewModel(){
    private val _walletBalanceState = MutableStateFlow<WalletBalanceState>(WalletBalanceState.Idle)
    val walletBalanceState : StateFlow<WalletBalanceState> = _walletBalanceState

    private val _paymentEvent = MutableSharedFlow<PaymentEvent>()
    val paymentEvent =
        _paymentEvent.asSharedFlow()
    private val _onlinePaymentEvent = MutableSharedFlow<OnlinePaymentEvent>()
    val onlinePaymentEvent =
        _onlinePaymentEvent.asSharedFlow()

    private val _paymentCallbackEvent =
        MutableSharedFlow<PaymentCallbackEvent>()

    val paymentCallbackEvent =
        _paymentCallbackEvent.asSharedFlow()
    fun getWalletBalance(){
        _walletBalanceState.value = WalletBalanceState.Loading
        viewModelScope.launch {
            val result = repository.getWalletBalance()
            result.onSuccess { balance ->
                _walletBalanceState.value = WalletBalanceState.Success(balance)
            }.onFailure { error ->
                _walletBalanceState.value =
                    WalletBalanceState.Error(error.message ?: "Failed to fetch wallet balance")
            }
        }
    }
    fun payWithWallet(bookingId: String){
        viewModelScope.launch {
            val result = paymentRepository.payWithWallet(bookingId)
            result.onSuccess {
                _paymentEvent.emit(PaymentEvent.NavigateToSuccess)
            }.onFailure { error ->
                _paymentEvent.emit(PaymentEvent.Message(error.message ?: "Failed to pay with wallet"))
            }
        }
    }

    fun payWithCash(bookingId: String){
        viewModelScope.launch {
            val result = paymentRepository.payWithCash(bookingId)
            result.onSuccess {
                _paymentEvent.emit(PaymentEvent.NavigateToSuccess)
            }.onFailure { error ->
                _paymentEvent.emit(PaymentEvent.Message(error.message ?: "Failed to pay with cash"))
            }
        }
    }

    fun createOrder(bookingId: String) {
        viewModelScope.launch {

            val result = paymentRepository.createOrder(bookingId)

            result.onSuccess { order ->

                _onlinePaymentEvent.emit(
                    OnlinePaymentEvent.OpenRazorpay(
                        orderId = order.orderId,
                        amount = order.amount,
                        currency = order.currency
                    )
                )

            }.onFailure { error ->

                _onlinePaymentEvent.emit(
                    OnlinePaymentEvent.Message(
                        error.message ?: "Failed to create order"
                    )
                )
            }
        }
    }

    fun verifyPayment(params: VerifyPaymentParams){
        viewModelScope.launch {
            Log.d("PAYMENT", "Verifying payment")
            val result = paymentRepository.verifyPayment(params)

            result.onSuccess {
                _onlinePaymentEvent.emit(OnlinePaymentEvent.NavigateToSuccess)
            }.onFailure { error ->
                _onlinePaymentEvent.emit(OnlinePaymentEvent.Message(error.message ?: "Failed to verify payment"))
            }
        }

    }
    fun onPaymentSuccess(
        paymentId: String,
        orderId: String,
        signature: String
    ) {
        Log.d("PAYMENT", "Payment Success")
        viewModelScope.launch {
            _paymentCallbackEvent.emit(
                PaymentCallbackEvent.Success(
                    paymentId = paymentId,
                    orderId = orderId,
                    signature = signature
                )
            )
        }
    }

    fun onPaymentError(message: String) {
        viewModelScope.launch {
            _paymentCallbackEvent.emit(
                PaymentCallbackEvent.Error(message)
            )
        }
    }

}