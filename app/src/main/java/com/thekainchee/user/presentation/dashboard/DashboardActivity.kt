package com.thekainchee.user.presentation.dashboard

import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityDashboardBinding
import com.thekainchee.user.presentation.base.SessionAwareActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity :  SessionAwareActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController
    private var backPressedTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.dashboardNavHost)
                    as NavHostFragment

        navController = navHostFragment.navController
        binding.quickBookingButton?.setOnClickListener {
            Snackbar.make(binding.root, "Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - backPressedTime < 2000) {
                finish()
            } else {
                backPressedTime = currentTime

                Snackbar.make(
                    binding.root,
                    "Press back again to exit",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}