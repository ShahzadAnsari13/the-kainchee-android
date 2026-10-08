package com.thekainchee.user.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thekainchee.user.data.local.datastore.UserPreferencesManager
import com.thekainchee.user.presentation.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
abstract class SessionAwareActivity : AppCompatActivity() {
    @Inject
    lateinit var tokenManager: UserPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tokenManager.isLoggedOut.collect { isLoggedOut ->
                    if (isLoggedOut) {
                        navigateToLogin()
                    }
                }
            }
        }

    }
    private fun navigateToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}