package com.alenear.user.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alenear.user.R
import com.alenear.user.presentation.auth.AuthActivity
import com.alenear.user.presentation.dashboard.DashboardActivity
import com.alenear.user.presentation.splash.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeSession()
    }

    private fun observeSession() {

        viewModel.checkUserSession { isLoggedIn ->

            val intent = if (isLoggedIn) {
                Intent(this, DashboardActivity::class.java)
            } else {
                Intent(this, AuthActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }








}