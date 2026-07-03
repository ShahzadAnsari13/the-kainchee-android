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
import com.thekainchee.user.R
import com.thekainchee.user.presentation.notification.state.NotificationState
import com.thekainchee.user.presentation.notification.viewModel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private val viewModel: NotificationViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationState.collectLatest { state ->

                    when (state) {
                        is NotificationState.Loading -> {

                        }

                        is NotificationState.Success -> {

                        }

                        is NotificationState.Empty -> {

                        }

                        is NotificationState.Error -> {

                        }

                        NotificationState.Idle -> Unit
                    }
                }
            }
        }
    }
}