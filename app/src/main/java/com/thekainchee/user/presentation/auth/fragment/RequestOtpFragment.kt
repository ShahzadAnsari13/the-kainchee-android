package com.thekainchee.user.presentation.auth.fragment


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentRequestOtpBinding
import com.thekainchee.user.presentation.auth.state.AuthState
import com.thekainchee.user.presentation.auth.viewModel.AuthViewModel
import com.thekainchee.user.presentation.auth.fragment.VerifyOtpFragment
import com.thekainchee.user.presentation.common.ui.countrypicker.CountryPickerBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestOtpFragment : Fragment() {
    private var _binding : FragmentRequestOtpBinding? = null
    private val binding get() =  _binding!!
    private val viewModel : AuthViewModel by viewModels()
    private var player: ExoPlayer? = null
    private val phoneRegex = Regex("^[6-9]\\d{9}$")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestOtpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRequestOtp.isEnabled = false
        binding.btnRequestOtp.alpha = 0.5f
        player = ExoPlayer.Builder(requireContext()).build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            Uri.parse("android.resource://${requireContext().packageName}/${R.raw.salon_bg}")
        )

        player?.setMediaItem(mediaItem)
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.prepare()
        player?.playWhenReady = true
        binding.etPhone.addTextChangedListener {
            val phone = it.toString().trim()
            binding.btnRequestOtp.isEnabled =   phoneRegex.matches(phone)
            binding.btnRequestOtp.alpha =
                if (phoneRegex.matches(phone)) 1f else 0.5f
        }
        fun openCountryPicker() {
            val picker = CountryPickerBottomSheet { country ->
                binding.etCountryCode.setText(country.dialCode)
            }
            picker.show(parentFragmentManager, "CountryPicker")
        }

        binding.etCountryCode.setOnClickListener {
            openCountryPicker()
        }

        binding.tilCountryCode.setEndIconOnClickListener {
            openCountryPicker()
        }

        binding.btnRequestOtp.setOnClickListener {
            val phone  = binding.etPhone.text.toString()
            if(!phoneRegex.matches(phone)){
                showToast("Enter phone number")
                return@setOnClickListener
            }
            val countryCode = binding.etCountryCode.text.toString()
            viewModel.requestOtp(countryCode,phone)
        }
        observeState()
    }

    private fun observeState(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.authState.collectLatest { state ->
                    when(state){
                        is AuthState.Loading -> {
                            binding.progressBar.isVisible =true
                            binding.etCountryCode.isEnabled = false
                            binding.btnRequestOtp.text = getString(R.string.sending)
                            binding.btnRequestOtp.isEnabled = false
                            binding.etPhone.isEnabled = false
                            binding.btnRequestOtp.alpha = 0.5f
                        }
                        is AuthState.OtpSent ->{
                            binding.progressBar.isVisible = false
                            binding.btnRequestOtp.text = getString(R.string.get_otp)
                            binding.btnRequestOtp.isEnabled = false
                            binding.etPhone.isEnabled = true
                            binding.btnRequestOtp.alpha = 0.5f

                            binding.etCountryCode.isEnabled = true
                            showToast(state.message)
                            openVerifyOtpFragment()
                            viewModel.resetState()
                        }
                        is AuthState.Error ->{
                            binding.progressBar.isVisible = false
                            binding.btnRequestOtp.text =  getString(R.string.get_otp)
                            val isValidPhone =
                                phoneRegex.matches(binding.etPhone.text.toString().trim())

                            binding.btnRequestOtp.isEnabled = isValidPhone
                            binding.btnRequestOtp.alpha = if (isValidPhone) 1f else 0.5f
                            binding.etPhone.isEnabled = true
                            binding.etCountryCode.isEnabled = true

                            showToast(state.message?: "Something went Wrong")

                        }
                        else ->{
                            binding.progressBar.isVisible = false
                            val isValidPhone =
                                phoneRegex.matches(binding.etPhone.text.toString().trim())

                            binding.btnRequestOtp.isEnabled = isValidPhone
                            binding.btnRequestOtp.alpha = if (isValidPhone) 1f else 0.5f
                            binding.btnRequestOtp.text = getString(R.string.get_otp)
                            binding.etCountryCode.isEnabled = true
                        }
                    }
                }
            }
        }
    }
    private fun openVerifyOtpFragment(){
        val action =
            RequestOtpFragmentDirections
                .actionRequestOtpFragmentToVerifyOtpFragment(
                    phone = binding.etPhone.text.toString(),
                    countryCode = binding.etCountryCode.text.toString()
                )

        findNavController().navigate(action)
    }
    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        player?.release()
        _binding = null
    }
}