package com.thekainchee.user.presentation.notification.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentNotificationBinding
import com.thekainchee.user.presentation.notification.adapter.NotificationAdapter
import com.thekainchee.user.presentation.notification.state.NotificationState
import com.thekainchee.user.presentation.notification.viewModel.NotificationViewModel
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NotificationAdapter()
        observeState()
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.shimmerLayout.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            viewModel.getNotifications()
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(), "No internet connection", Snackbar.LENGTH_SHORT).show()
            }else{
                viewModel.getNotifications()
            }
        }
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                viewModel.getNotifications()
            }
        }
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotificationFragment.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationState.collectLatest { state ->

                    when (state) {
                        is NotificationState.Loading -> {
                            binding.rvNotifications.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                            binding.layoutNoInternet.visibility = View.GONE
                            binding.layoutEmptyNotification.root.visibility = View.GONE
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.startShimmer()
                        }

                        is NotificationState.Success -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.rvNotifications.visibility = View.VISIBLE
                            adapter.submitList(state.notifications)
                        }

                        is NotificationState.Empty -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.layoutEmptyNotification.root.visibility = View.VISIBLE
                        }

                        is NotificationState.Error -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                        }

                        NotificationState.Idle -> Unit
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as ProfileActivity).setToolbarTitle("Notifications")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}