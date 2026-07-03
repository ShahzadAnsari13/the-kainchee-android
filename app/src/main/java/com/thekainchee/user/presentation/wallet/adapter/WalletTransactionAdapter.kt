package com.thekainchee.user.presentation.wallet.adapter

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemWalletTransactionBinding
import com.thekainchee.user.presentation.wallet.model.WalletTransactionUiModel

class WalletTransactionAdapter :
    ListAdapter<WalletTransactionUiModel, WalletTransactionAdapter.WalletTransactionViewHolder>(
        DiffCallback()
    ) {

    inner class WalletTransactionViewHolder(
        private val binding: ItemWalletTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: WalletTransactionUiModel) = with(binding) {

            tvReason.text = transaction.reason
            tvDescription.text = transaction.description
            tvDate.text = transaction.createdAt

            when (transaction.type) {

                "CREDIT" -> {

                    tvAmount.text = "+₹%.2f".format(transaction.amount)
                    tvAmount.setTextColor(
                        ContextCompat.getColor(root.context, R.color.wallet_credit)
                    )

                    chipTransactionType.text = "Credit"
                    chipTransactionType.setTextColor(
                        ContextCompat.getColor(root.context, R.color.wallet_credit)
                    )
                    chipTransactionType.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.wallet_credit_bg)
                        )

                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.wallet_credit_bg)
                    )

                    ivTransaction.setImageResource(R.drawable.ic_downward)
                    ivTransaction.setColorFilter(
                        ContextCompat.getColor(root.context, R.color.wallet_credit)
                    )
                }

                "DEBIT" -> {

                    tvAmount.text = "-₹%.2f".format(transaction.amount)
                    tvAmount.setTextColor(
                        ContextCompat.getColor(root.context, R.color.wallet_debit)
                    )

                    chipTransactionType.text = "Debit"
                    chipTransactionType.setTextColor(
                        ContextCompat.getColor(root.context, R.color.wallet_debit)
                    )
                    chipTransactionType.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.wallet_debit_bg)
                        )

                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.wallet_debit_bg)
                    )

                    ivTransaction.setImageResource(R.drawable.ic_upward)
                    ivTransaction.setColorFilter(
                        ContextCompat.getColor(root.context, R.color.wallet_debit)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletTransactionViewHolder {
        val binding = ItemWalletTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WalletTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletTransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WalletTransactionUiModel>() {

        override fun areItemsTheSame(
            oldItem: WalletTransactionUiModel,
            newItem: WalletTransactionUiModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WalletTransactionUiModel,
            newItem: WalletTransactionUiModel
        ) = oldItem == newItem
    }
}