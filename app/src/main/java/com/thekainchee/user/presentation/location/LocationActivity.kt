package com.thekainchee.user.presentation.location

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityLocationBinding
import com.thekainchee.user.presentation.base.SessionAwareActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationActivity :SessionAwareActivity() {
    private lateinit var binding: ActivityLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

    }
}