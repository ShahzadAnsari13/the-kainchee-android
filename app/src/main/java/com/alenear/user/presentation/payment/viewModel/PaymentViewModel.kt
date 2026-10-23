package com.alenear.user.presentation.payment.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alenear.user.domain.repository.PaymentRepository
import com.alenear.user.domain.repository.WalletRepository
import com.alenear.user.presentation.payment.model.VerifyPaymentParams
import com.alenear.user.presentation.payment.state.OnlinePaymentEvent
import com.alenear.user.presentation.payment.state.PaymentCallbackEvent
import com.alenear.user.presentation.payment.state.PaymentEvent
import com.alenear.user.presentation.payment.state.WalletBalanceState
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
                _paymentEvent.emit(PaymentEvent.Error(error.message ?: "Failed to pay with wallet"))
            }
        }
    }

    fun payWithCash(bookingId: String){
        viewModelScope.launch {
            val result = paymentRepository.payWithCash(bookingId)
            result.onSuccess {
                _paymentEvent.emit(PaymentEvent.NavigateToSuccess)
            }.onFailure { error ->
                _paymentEvent.emit(PaymentEvent.Error(error.message ?: "Failed to pay with cash"))
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
                    OnlinePaymentEvent.PaymentError(
                        error.message ?: "Failed to create order"
                    )
                )
            }
        }
    }
    fun verifyPayment(params: VerifyPaymentParams) {
        viewModelScope.launch {
            Log.d("PAYMENT", "Verifying payment")

            val result = paymentRepository.verifyPayment(params)

            result
                .onSuccess {
                    _onlinePaymentEvent.emit(
                        OnlinePaymentEvent.NavigateToSuccess
                    )
                }
                .onFailure { error ->
                    Log.d("PAYMENT", "HTTP error code = ${(error as? retrofit2.HttpException)?.code()}")
                    when {
                        error is retrofit2.HttpException &&
                                error.code() == 422 -> {
                                    Log.d("PAYMENT",error.code().toString())
                            _onlinePaymentEvent.emit(
                                OnlinePaymentEvent.PaymentFailed(
                                    "Payment not completed"
                                )
                            )
                        }

                        error is retrofit2.HttpException &&
                                error.code() == 500 -> {
                            _onlinePaymentEvent.emit(
                                OnlinePaymentEvent.PaymentError(
                                    "Server error. Please try again later."
                                )
                            )
                        }

                        error is java.io.IOException -> {
                            _onlinePaymentEvent.emit(
                                OnlinePaymentEvent.PaymentError(
                                    "Network error. Please try again."
                                )
                            )
                        }

                        else -> {
                            _onlinePaymentEvent.emit(
                                OnlinePaymentEvent.PaymentError(
                                    "Unable to verify payment. Please try again."
                                )
                            )
                        }
                    }
                }
        }
    }
    fun onPaymentSuccess(
        paymentId: String,
        orderId: String,
        signature: String
    ) {

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

    fun navigateToVerification() {
        viewModelScope.launch {
            _onlinePaymentEvent.emit(
                OnlinePaymentEvent.NavigateToVerification
            )
        }
    }

}