package com.example.ensiplant

import LoginActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ensiplant.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onStart() {
        super.onStart()
        // Jangan redirect ke LoginActivity lagi dari sini!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uploadPlantsToFirestore(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Pengecekan login & rememberMe
        checkLoginStatus()

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun checkLoginStatus() {
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val rememberMe = prefs.getBoolean("rememberMe", false)
        val currentUser = auth.currentUser

        Log.d("RememberMe", "rememberMe: $rememberMe, currentUser: ${currentUser?.email}")

        // Cek apakah user belum login atau tidak mencentang "Remember Me"
        if (currentUser == null || !rememberMe) {
            Log.d("MainActivity", "Redirecting to LoginActivity")

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Log.d("MainActivity", "User remembered and logged in, staying in MainActivity")
        }
    }
}
