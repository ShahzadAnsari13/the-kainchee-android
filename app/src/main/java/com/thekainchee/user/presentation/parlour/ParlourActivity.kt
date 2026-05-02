package com.thekainchee.user.presentation.parlour

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ActivityParlourBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParlourActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParlourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParlourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val parlourId = intent.getStringExtra("parlourId")
            ?: throw IllegalArgumentException("ParlourId missing")
        val distance = intent.getStringExtra("distance")
            ?: throw IllegalArgumentException("distance missing")
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.parlourNavHost) as NavHostFragment

        val navController = navHostFragment.navController

        val bundle = Bundle().apply {
            putString("parlourId", parlourId)
            putString("distance", distance)
        }
        Log.d("CHECK", "parlourId = $parlourId")
        navController.setGraph(R.navigation.parlour_nav_graph, bundle)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}