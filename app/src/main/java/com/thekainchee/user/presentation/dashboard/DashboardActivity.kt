package com.thekainchee.user.presentation.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityDashboardBinding
import com.thekainchee.user.presentation.base.SessionAwareActivity
import com.thekainchee.user.presentation.dashboard.home.fragment.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity :  SessionAwareActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState == null){
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when(item.itemId){

                R.id.menu_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_booking -> {
                    Toast.makeText(this, "Bookings", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_categorie -> {
                    Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }

        }
        supportFragmentManager.addOnBackStackChangedListener {

            val fragment = supportFragmentManager.findFragmentById(R.id.container)

            when (fragment?.tag) {

                "HOME" -> binding.bottomNavigation.selectedItemId = R.id.menu_home

                "BOOKING" -> binding.bottomNavigation.selectedItemId = R.id.menu_booking

                "CATEGORY" -> binding.bottomNavigation.selectedItemId = R.id.menu_categorie
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .commit()
    }

}