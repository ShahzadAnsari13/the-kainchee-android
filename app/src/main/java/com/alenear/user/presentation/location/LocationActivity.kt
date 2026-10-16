package com.alenear.user.presentation.location

import android.os.Bundle
import com.alenear.user.databinding.ActivityLocationBinding
import com.alenear.user.presentation.base.SessionAwareActivity
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