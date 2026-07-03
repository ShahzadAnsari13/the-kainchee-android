package com.thekainchee.user.presentation.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.action_notification -> {
                    // TODO
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
    fun clearMenu() {
        binding.toolbar.menu.clear()
    }
}