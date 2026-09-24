package com.thekainchee.user.presentation.referral.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemReferralHistoryBinding
import com.thekainchee.user.presentation.referral.model.ReferralItem

class ReferralHistoryAdapter(
    private val referrals: List<ReferralItem>
) : RecyclerView.Adapter<ReferralHistoryAdapter.ReferralViewHolder>() {

    inner class ReferralViewHolder(
        private val binding: ItemReferralHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(referral: ReferralItem) {

            binding.tvReferralInitial.text = "R"

            binding.tvReferralPhone.text =
                referral.referredPhoneNumber

            binding.tvReferralDate.text =
                referral.createdAt

            binding.tvReferralReward.text =
                if (referral.rewardAmount > 0) {
                    "+₹${referral.rewardAmount}"
                } else {
                    ""
                }

            binding.tvReferralStatus.apply {
                text = referral.referralStatus

                if (
                    referral.referralStatus == "PENDING" ||
                    referral.referralStatus == "COMPLETED"
                ) {
                    setBackgroundResource(
                        R.drawable.bg_status_confirmed
                    )

                    setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.primaryColor
                        )
                    )
                } else {
                    setBackgroundResource(
                        R.drawable.bg_status_cancelled
                    )

                    setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.booking_cancelled
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReferralViewHolder {

        val binding = ItemReferralHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ReferralViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReferralViewHolder,
        position: Int
    ) {
        holder.bind(referrals[position])
    }

    override fun getItemCount(): Int = referrals.size
}