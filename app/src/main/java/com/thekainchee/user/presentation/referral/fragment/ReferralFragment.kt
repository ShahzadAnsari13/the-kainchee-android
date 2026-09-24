package com.thekainchee.user.presentation.referral.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentReferralBinding
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.presentation.referral.bottomSheet.WithdrawReferralRewardBottomSheet
import com.thekainchee.user.presentation.referral.model.ReferralHistory
import com.thekainchee.user.presentation.referral.model.ReferralItem
import com.thekainchee.user.presentation.referral.state.ReferralHistoryState
import com.thekainchee.user.presentation.referral.state.WithdrawReferralState
import com.thekainchee.user.presentation.referral.viewModel.ReferralViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ReferralFragment : Fragment() {
    private var _binding: FragmentReferralBinding? = null
    private val binding get() = _binding!!
    private var referralImageUri: Uri? = null
    private val viewModel: ReferralViewModel by viewModels()
    private lateinit var currentReferralHistory: ReferralHistory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReferralBinding.inflate(
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

        setupClickListeners()
        observeReferralState()
        lifecycleScope.launch {
            referralImageUri = withContext(Dispatchers.IO) {
                getReferralImageUri()
            }
        }
        withInternet {
            viewModel.getReferralHistory()
        }

    }

    private fun setupClickListeners() {

        binding.btnWithdrawReward.setOnClickListener {
            val reward = currentReferralHistory?.currentReferralRewards ?: 0

            if (reward > 0) {
                WithdrawReferralRewardBottomSheet
                    .newInstance(reward)
                    .show(
                        parentFragmentManager,
                        "WithdrawReferralRewardBottomSheet"
                    )
            }
        }

        binding.tvViewAll.setOnClickListener {
            val action =
                ReferralFragmentDirections
                    .actionReferralFragmentToReferralHistoryFragment(
                        currentReferralHistory
                    )

            findNavController().navigate(action)
        }

        binding.ivCopyReferralCode.setOnClickListener {
            // Copy referral code
            copyReferralCode()
        }

        binding.btnShareReferral.setOnClickListener {
            shareReferral()
        }
    }
    private fun shareReferral() {

        val referralCode = binding.tvReferralCode.text.toString()

        val shareText = """
        Hey! 👋

        I'm using THE KAINCHEE for salon & beauty bookings. You can use it too ❤️

        🎁 You'll Get
        • Refer friends & earn rewards
        • Earn up to ₹150 in referral rewards

        ⚡ How to get it:
        • Use my referral code: $referralCode
        • Download THE KAINCHEE app using the link below
        • Sign up and enter my referral code

        🔗 Download the app:
        https://play.google.com/store/apps/details?id=com.thekainchee.user
    """.trimIndent()

        val imageUri = referralImageUri

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(
            Intent.createChooser(intent, "Share via")
        )
    }
    private fun getReferralImageUri(): Uri {

        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_referral_share
        )

        val file = File(
            requireContext().cacheDir,
            "referral_share.png"
        )

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
    }
    private fun copyReferralCode() {

        val referralCode = binding.tvReferralCode.text.toString()

        val clipboard =
            requireContext().getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        val clip = ClipData.newPlainText(
            "Referral Code",
            referralCode
        )

        clipboard.setPrimaryClip(clip)

    }
    private fun observeReferralState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.referralHistoryState.collect { state ->

                    when (state) {

                        ReferralHistoryState.Idle -> Unit

                        ReferralHistoryState.Loading -> {
                            binding.referralContent.visibility = View.GONE
                            binding.stateView.hide()
                            binding.referralShimmer.visibility = View.VISIBLE
                            binding.referralShimmer.startShimmer()
                        }

                        is ReferralHistoryState.Success -> {
                            binding.referralShimmer.stopShimmer()
                            binding.referralShimmer.visibility = View.GONE
                            binding.stateView.hide()
                            binding.referralContent.visibility = View.VISIBLE
                            currentReferralHistory = state.data
                            bindReferralData(state.data)
                        }

                        is ReferralHistoryState.Error -> {
                            binding.referralShimmer.stopShimmer()
                            binding.referralShimmer.visibility = View.GONE
                            binding.referralContent.visibility = View.GONE
                            showReferralLoadError {
                                withInternet {
                                    viewModel.getReferralHistory()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun showNoInternetState(
        retryText: String = "Retry",
        onRetry: () -> Unit
    ){
        binding.stateView.show(
            StateViewData(
                image = R.drawable.no_internet,
                title = "No Internet Connection",
                subtitle = "Please check your internet connection and try again.",
                primaryButtonText = retryText,
                onPrimaryClick = onRetry
            )
        )
    }
    private fun withInternet(
        onConnected: () -> Unit
    ) {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showNoInternetState {
                if (!NetworkUtils.isInternetAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.stateView.hide()
                    onConnected()
                }
            }
        } else {
            onConnected()
        }
    }
    private fun showReferralLoadError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Oops! Something went wrong",
                subtitle = "We couldn't load your referral details. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry
            )
        )
    }


    private fun bindReferralData(data: ReferralHistory) {

        binding.tvReferralCode.text = data.referralCode

        binding.tvCurrentReferrals.text =
            data.currentReferrals.toString()

        binding.tvCurrentReferralRewards.text =
            "₹${data.currentReferralRewards}"

        updateMilestoneProgress(data.currentReferrals)
        data.nextMilestone?.let { milestone ->

            binding.tvNextMilestone.text =
                "${milestone.referralCount}${getOrdinalSuffix(milestone.referralCount)} Referral"

            binding.tvNextMilestoneReward.text =
                "₹${milestone.reward}"

            val remaining =
                milestone.referralCount - data.currentReferrals

            binding.tvNextMilestoneMessage.text =
                if (remaining > 0) {
                    "Refer $remaining more friend${if (remaining > 1) "s" else ""} to earn"
                } else {
                    "Milestone completed"
                }
        }
        binding.tvViewAll.isVisible = data.referrals.size > 1
        val latestReferral = data.referrals.firstOrNull()

        if (latestReferral != null) {
            bindLatestReferral(latestReferral)
        } else {
            binding.cardReferralItem1.isVisible = false
        }


        binding.btnWithdrawReward.isVisible =
            data.currentReferralRewards > 0
    }

    private fun bindLatestReferral(referral: ReferralItem) {

        binding.cardReferralItem1.isVisible = true


        binding.tvReferralPhone1.text =
            referral.referredPhoneNumber

        binding.tvReferralDate1.text =
            referral.createdAt

        binding.tvReferralReward1.text =
            if (referral.rewardAmount > 0) {
                "+₹${referral.rewardAmount}"
            } else {
                ""
            }
        if(referral.referralStatus=="PENDING" || referral.referralStatus=="COMPLETED"){
            binding.tvReferralStatus1.apply {
                setBackgroundResource(R.drawable.bg_status_confirmed)
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryColor
                    )
                )
            }
        }else{
            binding.tvReferralStatus1.apply {
                setBackgroundResource(R.drawable.bg_status_cancelled)
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.booking_cancelled
                    )
                )
            }
        }
        binding.tvReferralStatus1.text =
            referral.referralStatus
    }

    private fun updateMilestoneProgress(currentReferrals: Int) {

        val progressViews = listOf(
            binding.milestoneProgress1,
            binding.milestoneProgress2,
            binding.milestoneProgress3,
            binding.milestoneProgress4,
            binding.milestoneProgress5
        )

        val tickViews = listOf(
            binding.milestoneTick1,
            binding.milestoneTick2,
            binding.milestoneTick3,
            binding.milestoneTick4,
            binding.milestoneTick5
        )

        progressViews.forEachIndexed { index, view ->

            val completed = index < currentReferrals

            view.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (completed) {
                        R.color.primaryColor
                    } else {
                        R.color.light_stroke
                    }
                )
            )

            tickViews[index].text =
                if (completed) "✓" else ""

            tickViews[index].setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (completed) {
                        R.color.primaryColor
                    } else {
                        R.color.light_stroke
                    }
                )
            )
        }
    }
    private fun getOrdinalSuffix(number: Int): String {
        return when {
            number % 10 == 1 -> "st"
            number % 10 == 2 -> "nd"
            number % 10 == 3 -> "rd"
            else -> "th"
        }
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as ProfileActivity).setToolbarTitle("Refer & Earn")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}