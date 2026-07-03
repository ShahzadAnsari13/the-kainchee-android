package com.thekainchee.user.presentation.booking

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityBookingBinding
import com.thekainchee.user.presentation.payment.viewModel.PaymentViewModel
import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private var bookingPreviewData: BookingPreviewUiModel? = null

    private val paymentViewModel: PaymentViewModel by viewModels()
    private lateinit var binding: ActivityBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openMyBookings = intent.getBooleanExtra("openMyBookings", false)

        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bookingPreviewData =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                intent.getParcelableExtra(
                    "bookingPreviewData",
                    BookingPreviewUiModel::class.java
                )

            } else {

                @Suppress("DEPRECATION")
                intent.getParcelableExtra("bookingPreviewData")
            }

        val bookingNavHostFragment =
            supportFragmentManager.findFragmentById(R.id.bookingNavHost) as NavHostFragment


        val navController = bookingNavHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.booking_nav_graph)

        if (openMyBookings) {
            graph.setStartDestination(R.id.myBookingFragment)
            navController.graph = graph
        } else {
            graph.setStartDestination(R.id.bookingSlotFragment)

            val bundle = Bundle().apply {
                putParcelable("services", bookingPreviewData)
            }

            navController.setGraph(graph, bundle)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?,
                                  paymentData: PaymentData?) {
        paymentViewModel.onPaymentSuccess(
            paymentId = razorpayPaymentId.orEmpty(),
            orderId = paymentData?.orderId.orEmpty(),
            signature = paymentData?.signature.orEmpty()
        )
    }

    override fun onPaymentError(
        code: Int,
        response: String?,
        paymentData: PaymentData?
    ) {
        paymentViewModel.onPaymentError(
            response ?: "Payment Failed"
        )
    }
    fun showToolbar(show: Boolean) {
        binding.toolbar.visibility =
            if (show) View.VISIBLE else View.GONE
    }
    fun setToolbarTitle(title: String) {
        binding.tvTitle.text = title
    }


}