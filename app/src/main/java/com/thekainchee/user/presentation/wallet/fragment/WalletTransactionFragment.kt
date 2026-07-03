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
        // Inflate the layout for this fragment
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
            binding.shimmerLayout.visibility = View.GONE
            binding.rvTransactions.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE

        }else{
            walletViewModel.getWalletTransactions()
        }
        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                walletViewModel.getWalletTransactions()
            }
        }
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                walletViewModel.getWalletTransactions()
            }
        }
    }
    private fun observeWalletTransactions() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                walletViewModel.walletTransactionState.collect { state ->

                    when (state) {

                        is WalletTransactionUiState.Idle -> Unit

                        is WalletTransactionUiState.Loading -> {
                            binding.rvTransactions.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                            binding.layoutNoInternet.visibility = View.GONE
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.startShimmer()
                        }

                        is WalletTransactionUiState.Success -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.rvTransactions.visibility = View.VISIBLE
                            walletTransactionAdapter.submitList(state.transactions)
                        }

                        is WalletTransactionUiState.Empty -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvEmptyTitle.text = "No Transactions Yet"
                            binding.tvEmptySubtitle.text = "Your wallet transactions will appear here once you start using your wallet."
                            binding.btnRetry.visibility = View.GONE
                            walletTransactionAdapter.submitList(emptyList())
                        }

                        is WalletTransactionUiState.Error -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvEmptyTitle.text = "Something Went Wrong"
                            binding.tvEmptySubtitle.text = "We couldn't load your wallet transactions. Please try again."

                        }
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}