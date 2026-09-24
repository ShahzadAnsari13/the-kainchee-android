package com.thekainchee.user.presentation.referral.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentReferralHistoryBinding
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.presentation.referral.adapter.ReferralHistoryAdapter
import com.thekainchee.user.presentation.referral.model.ReferralHistory
import com.thekainchee.user.presentation.referral.viewModel.ReferralViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferralHistoryFragment : Fragment() {

    private var _binding: FragmentReferralHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var referralHistoryAdapter: ReferralHistoryAdapter
    private val args: ReferralHistoryFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReferralHistoryBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        val data = args.referralHistory
        bindReferralHistory(data)

    }
    private fun setupRecyclerView() {
        referralHistoryAdapter = ReferralHistoryAdapter(emptyList())

        binding.rvReferralHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = referralHistoryAdapter
            setHasFixedSize(true)
        }
    }
    private fun bindReferralHistory(data: ReferralHistory) {

        binding.tvTotalReferrals.text =
            data.totalReferrals.toString()

        binding.tvTotalRewards.text =
            "₹${data.totalReferralRewards}"

        binding.tvReferralCount.text =
            "${data.referrals.size} referrals"

        val adapter = ReferralHistoryAdapter(data.referrals)

        binding.rvReferralHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as ProfileActivity).setToolbarTitle("Referral History")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}