package com.thekainchee.user.presentation.wallet.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.WalletRepository
import com.thekainchee.user.presentation.wallet.state.WalletTransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _walletTransactionState =
        MutableStateFlow<WalletTransactionUiState>(
            WalletTransactionUiState.Idle
        )
    val walletTransactionState = _walletTransactionState.asStateFlow()
    private var walletTransactionJob: Job? = null
    fun getWalletTransactions() {
        walletTransactionJob?.cancel()
        walletTransactionJob = viewModelScope.launch {

            _walletTransactionState.value =
                WalletTransactionUiState.Loading

            repository.getWalletTransactions()
                .onSuccess { transactions ->

                    if (transactions.isEmpty()) {

                        _walletTransactionState.value =
                            WalletTransactionUiState.Empty

                    } else {

                        _walletTransactionState.value =
                            WalletTransactionUiState.Success(transactions)
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) return@onFailure
                    _walletTransactionState.value =
                        WalletTransactionUiState.Error(
                            error.message ?: "Failed to fetch wallet transactions"
                        )
                }
        }
    }
}