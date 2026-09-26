package com.thekainchee.user.presentation.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityDashboardBinding
import com.thekainchee.user.presentation.base.SessionAwareActivity
import com.thekainchee.user.presentation.booking.fragment.MyBookings
import com.thekainchee.user.presentation.dashboard.home.fragment.HomeFragment
import com.thekainchee.user.presentation.profile.fragment.MyProfileFragment
import com.thekainchee.user.presentation.wallet.fragment.WalletTransactionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity :  SessionAwareActivity() {
    private lateinit var binding: ActivityDashboardBinding
    var isBottomNavVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState == null){
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.menu_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.menu_booking -> {
                    loadFragment(MyBookings())
                    true
                }

                R.id.menu_wallet -> {
                    loadFragment(WalletTransactionFragment())
                    true
                }

                R.id.menu_profile -> {
                    loadFragment(MyProfileFragment())
                    true
                }

                else -> false
            }
        }
        binding.quickBookingButton?.setOnClickListener {
            Snackbar.make(binding.root, "Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this) {

            if (binding.bottomNavigation.selectedItemId != R.id.menu_home) {

                binding.bottomNavigation.selectedItemId = R.id.menu_home
                loadFragment(HomeFragment())

            } else {
                finish()
            }
        }
    }



    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}