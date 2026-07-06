package com.thekainchee.user.presentation.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.profileNavHost) as NavHostFragment

        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id == R.id.notificationFragment) {
                binding.toolbar.menu.findItem(R.id.action_notification)?.isVisible = false
            } else {
                binding.toolbar.menu.findItem(R.id.action_notification)?.isVisible = true
            }
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.action_notification -> {
                    navController.navigate(R.id.notificationFragment)
                    true
                }

                R.id.action_settings -> {
                    // TODO
                    true
                }

                else -> false
            }
        }
    }
    fun setToolbarTitle(title: String) {
        binding.toolbar.title = title
    }
    fun clearMenu() {
        binding.toolbar.menu.clear()
    }
}