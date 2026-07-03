package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toUiModel
import com.thekainchee.user.data.remote.api.WalletApi
import com.thekainchee.user.domain.repository.WalletRepository
import com.thekainchee.user.presentation.wallet.model.WalletTransactionUiModel
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(private val api: WalletApi) : WalletRepository  {
    override suspend fun getWalletBalance(): Result<Double> {
        return try{
            val response = api.getWalletBalance()
            if(response.isSuccessful && response.body() != null){
                val body = response.body()
                val balance = body?.balance ?: 0.0
                Result.success(balance)
            }else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch service categories"))
            }
        }catch(e: Exception){
                Result.failure(e)
        }
    }

    override suspend fun getWalletTransactions(): Result<List<WalletTransactionUiModel>> {
        return try {
            val response = api.getWalletTransactions()

            if (response.isSuccessful && response.body() != null) {

                val transactions = response.body()!!.data.map {
                    it.toUiModel()
                }

                Result.success(transactions)

            } else {

                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(
                    Exception(error.message ?: "Failed to fetch wallet transactions")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}