package com.thekainchee.user.presentation.auth.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentVerifyOtpBinding
import com.thekainchee.user.presentation.auth.state.AuthState
import com.thekainchee.user.presentation.auth.viewModel.AuthViewModel
import com.thekainchee.user.presentation.dashboard.DashboardActivity
import com.thekainchee.user.presentation.profile.viewModel.ProfileViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {
    private var _binding : FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel : AuthViewModel by viewModels()
    private var phone: String? = null
    private var countryCode: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var player: ExoPlayer? = null
    private val args : VerifyOtpFragmentArgs by navArgs()

    private val profileViewModel : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phone = args.phone
        countryCode = args.countryCode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentVerifyOtpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.layoutNoInternet.isVisible = true
            binding.mainContent.isVisible = false
        }else{
            startResendTimer()
            setupVideoPlayerAndSubTitle()
        }
        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                binding.layoutNoInternet.isVisible = false
                binding.mainContent.isVisible = true
                if (player == null) {
                    setupVideoPlayerAndSubTitle()
                }
                if (countDownTimer==null){
                    startResendTimer()
                }
            }
        }



        binding.btnVerifyOtp.setOnClickListener {

            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.isVisible = true
                binding.mainContent.isVisible = false
                return@setOnClickListener
            }
            else{
                val otp = binding.pinViewOtp.text.toString()
                if(otp.length != 6){
                    Toast.makeText(requireContext(),"Enter valid OTP", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(viewModel.authState.value is AuthState.Loading) return@setOnClickListener
                val code = countryCode ?: return@setOnClickListener
                val number = phone ?: return@setOnClickListener
                viewModel.verifyOtp(code,number,otp)
            }
        }

        binding.pinViewOtp.doAfterTextChanged {
            val otp = it.toString()

            binding.btnVerifyOtp.isEnabled = otp.length == 6
            binding.btnVerifyOtp.alpha = if (otp.length == 6) 1f else 0.5f
            if (otp.length == 6) {

                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    binding.layoutNoInternet.isVisible = true
                    binding.mainContent.isVisible = false
                }else{
                    if(viewModel.authState.value is AuthState.Loading) return@doAfterTextChanged

                    val code = countryCode ?: return@doAfterTextChanged
                    val number = phone ?: return@doAfterTextChanged

                    viewModel.verifyOtp(code, number, otp)
                }

            }
        }
        observeState()
    }


    private fun setupVideoPlayerAndSubTitle(){
        //Player Setup
        player = ExoPlayer.Builder(requireContext()).build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            Uri.parse("android.resource://${requireContext().packageName}/${R.raw.salon_bg2}")
        )

        player?.setMediaItem(mediaItem)
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.prepare()
        player?.playWhenReady = true


        //SubTitle SetUp
        val text = "Sent to ${countryCode ?: ""} ${phone ?: ""} "
        val start = "Sent to ".length
        val spannable = SpannableString(text + " ")


        // phone number color
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(requireContext(), R.color.black)
            ),
            start,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        // phone number bold
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        //edit icon
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)
        val size  = 60
        drawable?.setBounds(0,0,size,size)

        drawable?.let {
            spannable.setSpan(
                ImageSpan(it, ImageSpan.ALIGN_BOTTOM),
                text.length,
                text.length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }


        // icon clickable
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    parentFragmentManager.popBackStack()
                }
            },
            text.length,
            text.length + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        binding.tvSubtitle.movementMethod = LinkMovementMethod.getInstance()

        binding.tvSubtitle.text =spannable
    }

    private fun startResendTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val second = millisUntilFinished / 1000
                val formatted = String.format("%02d", second)

                binding.tvResendOtp.text = "Resend OTP in 00:$formatted"
            }

            override fun onFinish() {

                val text = "Didn't receive OTP? Resend OTP"
                val spannable = SpannableString(text)

                val start = text.indexOf("Resend OTP")
                binding.tvResendOtp.highlightColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primaryColor)),
                    start,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {


                            val code = countryCode ?: return
                            val number = phone ?: return

                            viewModel.requestOtp(code, number)
                        }
                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    },
                    start,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.tvResendOtp.text = spannable
                binding.tvResendOtp.movementMethod = LinkMovementMethod.getInstance()
            }

        }.start()
    }

    private  fun observeState(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.authState.collectLatest { state ->
                    when(state){
                        is AuthState.Loading->{
                            binding.progressBar.isVisible = true
                            binding.btnVerifyOtp.isEnabled = false
                        }
                        is AuthState.OtpSent -> {
                            binding.progressBar.isVisible = false
                            binding.btnVerifyOtp.isEnabled = false
                            startResendTimer()

                            Toast.makeText(requireContext(), "OTP sent again", Toast.LENGTH_SHORT).show()

                        }
                        is AuthState.OtpVerified -> {

                            countDownTimer?.cancel()
                            countDownTimer = null
                            binding.progressBar.isVisible = false
                            binding.btnVerifyOtp.isEnabled = false

                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        profileViewModel.updateFcmToken(task.result)
                                    }
                                    startActivity(Intent(requireContext(), DashboardActivity::class.java))
                                    requireActivity().finish()
                                }
                        }
                        is AuthState.Error->{

                            binding.progressBar.isVisible = false
                            binding.btnVerifyOtp.isEnabled = true
                            Toast.makeText(requireContext(), state.message ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                        else -> {

                            binding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        countDownTimer?.cancel()
        countDownTimer = null
        player?.release()
        player = null
        _binding = null
    }
}