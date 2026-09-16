package com.thekainchee.user.presentation.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentWalletTransactionBinding
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.presentation.wallet.adapter.WalletTransactionAdapter
import com.thekainchee.user.presentation.wallet.state.WalletTransactionUiState
import com.thekainchee.user.presentation.wallet.viewModel.WalletViewModel
import com.thekainchee.user.utils.NetworkUtils
import kotlinx.coroutines.launch
class WalletTransactionFragment : Fragment() {
    private var _binding: FragmentWalletTransactionBinding? = null
    private val binding get() = _binding!!
    private val walletViewModel: WalletViewModel by activityViewModels()
    private lateinit var walletTransactionAdapter: WalletTransactionAdapter
    private val args : WalletTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletTransactionAdapter = WalletTransactionAdapter()
        binding.walletCard.tvWalletBalance.text = "₹%.2f".format(args.balance)
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = walletTransactionAdapter
            setHasFixedSize(true)
        }
        observeWalletTransactions()

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.rvTransactions.visibility = View.GONE
            showNoInternetState("Try Again"){
                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else{
                    binding.stateView.hide()
                    walletViewModel.getWalletTransactions()
                }
            }
        }else{
            walletViewModel.getWalletTransactions()
        }

    }
    private fun observeWalletTransactions() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                walletViewModel.walletTransactionState.collect { state ->

                    when (state) {

                        is WalletTransactionUiState.Idle -> Unit

                        is WalletTransactionUiState.Loading -> {
                            binding.stateView.hide()
                            binding.rvTransactions.visibility = View.GONE
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.startShimmer()
                        }

                        is WalletTransactionUiState.Success -> {
                            binding.stateView.hide()
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.rvTransactions.visibility = View.VISIBLE
                            walletTransactionAdapter.submitList(state.transactions)
                        }

                        is WalletTransactionUiState.Empty -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            showTransactionEmpty()
                            walletTransactionAdapter.submitList(emptyList())
                        }

                        is WalletTransactionUiState.Error -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            showTransactionError { withInternet {
                                binding.stateView.hide()
                                walletViewModel.getWalletTransactions() } }

                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as ProfileActivity).setToolbarTitle("Transactions")
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

    private fun showTransactionError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to load transactions",
                subtitle = "We couldn't load your wallet transactions right now. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry
            )
        )
    }
    private fun showTransactionEmpty(

    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "No Transactions Yet",
                subtitle = "You don't have any wallet transactions yet."
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}