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
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentRequestOtpBinding
import com.thekainchee.user.presentation.auth.AuthState
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
    private lateinit var player: ExoPlayer
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
        player = ExoPlayer.Builder(requireContext()).build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            Uri.parse("android.resource://${requireContext().packageName}/${R.raw.salon_bg}")
        )

        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.prepare()
        player.play()
        binding.etPhone.addTextChangedListener {
            val phone = it.toString()
            binding.btnRequestOtp.isEnabled =   phoneRegex.matches(phone)
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
            if(phone.isBlank()){
                Toast.makeText(requireContext(),"Enter phone number", Toast.LENGTH_SHORT).show()
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
                            binding.btnRequestOtp.text = "Sending..."
                            binding.btnRequestOtp.isEnabled = false
                        }
                        is AuthState.OtpSent ->{
                            binding.progressBar.isVisible = false
                            binding.btnRequestOtp.text = "Get OTP"
                            binding.btnRequestOtp.isEnabled = false
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                            openVerifyOtpFragment()
                            viewModel.resetState()
                        }
                        is AuthState.Error ->{
                            binding.progressBar.isVisible = false
                            binding.btnRequestOtp.text = "Get OTP"
                            binding.btnRequestOtp.isEnabled = true
                            Toast.makeText(requireContext(),state.message?: "Something went Wrong",
                                Toast.LENGTH_SHORT).show()

                        }
                        else ->{
                            binding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }
    private fun openVerifyOtpFragment(){
        val phone = binding.etPhone.text.toString()
        val countryCode = binding.etCountryCode.text.toString()
        val fragment = VerifyOtpFragment()
        val bundle = Bundle()
        bundle.putString("phone",phone)
        bundle.putString("countryCode",countryCode)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroyView() {
        player.release()
        _binding = null
        super.onDestroyView()
    }
}