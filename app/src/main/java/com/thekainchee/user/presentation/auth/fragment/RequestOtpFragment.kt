package com.thekainchee.user.presentation.auth.fragment


import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentRequestOtpBinding
import com.thekainchee.user.presentation.auth.bottomSheet.ReferralCodeBottomSheet
import com.thekainchee.user.presentation.auth.state.AuthState
import com.thekainchee.user.presentation.auth.viewModel.AuthViewModel
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.common.ui.countrypicker.CountryPickerBottomSheet
import com.thekainchee.user.utils.NetworkUtils
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

        if (player == null) {
            setupVideoPlayer()
        }

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
            withInternet {
                val phone  = binding.etPhone.text.toString()
                if(!phoneRegex.matches(phone)){
                    showSnackbar("Enter phone number")
                    return@withInternet
                }
                val countryCode = binding.etCountryCode.text.toString()
                viewModel.requestOtp(countryCode,phone)
            }


        }
        binding.cardReferral.setOnClickListener {

            val phone = binding.etPhone.text
                .toString()
                .trim()

            if (!phoneRegex.matches(phone)) {
                showSnackbar("Please enter a valid phone number")
                binding.etPhone.requestFocus()
                return@setOnClickListener
            }

            ReferralCodeBottomSheet
                .newInstance(phone)
                .show(
                    childFragmentManager,
                    "ReferralCodeBottomSheet"
                )
        }
        setupTermsAndPrivacy()

        observeState()
    }
    private fun setupTermsAndPrivacy() {

        val text = "By continuing, you agree to our Terms of Service and Privacy Policy"

        val spannable = SpannableString(text)

        val green = ContextCompat.getColor(requireContext(), R.color.primaryColor)

        val termsText = "Terms of Service"
        val privacyText = "Privacy Policy"

        val termsStart = text.indexOf(termsText)
        val privacyStart = text.indexOf(privacyText)

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(
                        requireContext(),
                        "Terms of Service clicked",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = green
                    ds.isUnderlineText = false
                }
            },
            termsStart,
            termsStart + termsText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(
                        requireContext(),
                        "Privacy Policy clicked",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = green
                    ds.isUnderlineText = false
                }
            },
            privacyStart,
            privacyStart + privacyText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannable
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerms.highlightColor = Color.TRANSPARENT
    }
    private fun setupVideoPlayer(){
        player = ExoPlayer.Builder(requireContext()).build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            Uri.parse("android.resource://${requireContext().packageName}/${R.raw.salon_bg}")
        )

        player?.setMediaItem(mediaItem)
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.prepare()
        player?.playWhenReady = true
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
                            showSnackbar(state.message)
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

                            showSnackbar(state.message?: "Something went Wrong")

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
    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        player?.release()
        player = null
        _binding = null
    }
}