package com.thekainchee.user.presentation.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityAuthBinding
import com.thekainchee.user.databinding.ActivityMainBinding
import com.thekainchee.user.presentation.auth.fragment.RequestOtpFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, RequestOtpFragment())
                .commit()
        }
    }
}