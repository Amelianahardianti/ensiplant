package com.example.ensiplant

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ensiplant.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi dan gunakan ViewBinding untuk menampilkan layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Setup Navigasi
        setupNavigation()

        // Pengecekan pengguna yang sudah login (kode Firebase-mu)
        checkCurrentUser()

        // Menangani navigasi untuk pengguna baru
        handleNewUserNavigation()
    }

    private fun setupNavigation() {
        // Mencari NavHostFragment dari layout activity_main.xml
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment

        // Mendapatkan NavController dari NavHostFragment
        navController = navHostFragment.navController

        // Menghubungkan BottomNavigationView dengan NavController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun handleNewUserNavigation() {
        val isNewUser = intent.getBooleanExtra("IS_NEW_USER", false)
        if (isNewUser) {
            navController.navigate(R.id.editProfileFragment)
        }
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("AuthCheck", "User is logged in: ${currentUser.email}")
        } else {
            Log.d("AuthCheck", "No user is logged in")
        }
    }
}
