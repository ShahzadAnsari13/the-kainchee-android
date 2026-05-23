package com.thekainchee.user.presentation.booking

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityBookingBinding
import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingActivity : AppCompatActivity() {
    private var bookingPreviewData: BookingPreviewUiModel? = null
    private lateinit var binding: ActivityBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        val bundle = Bundle().apply {
            putParcelable("services", bookingPreviewData)
        }
        navController.setGraph(R.navigation.booking_nav_graph, bundle)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }



}