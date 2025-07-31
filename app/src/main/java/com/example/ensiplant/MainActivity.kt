package com.example.ensiplant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ensiplant.databinding.ActivityMainBinding
import com.example.ensiplant.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        // Pengecekan penting: jika user belum login, langsung arahkan ke LoginActivity
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return // Hentikan eksekusi onCreate agar tidak terjadi error
        }

        // Jika user sudah login, baru lanjutkan untuk menampilkan layout MainActivity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigasi
        setupNavigation()

        // (Opsional) Menangani navigasi untuk pengguna baru jika diperlukan
        handleNewUserNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        // Logika untuk menyembunyikan/menampilkan navigasi bawah
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Tampilkan Navigasi Bawah di halaman utama
                R.id.forumFragment,
                R.id.encyclopediaFragment,
                R.id.profileFragment -> {
                    binding.cardViewBottomNav.visibility = View.VISIBLE
                }
                // Sembunyikan di halaman lain (detail, edit, create, dll.)
                else -> {
                    binding.cardViewBottomNav.visibility = View.GONE
                }
            }
        }
    }

    private fun handleNewUserNavigation() {
        val isNewUser = intent.getBooleanExtra("IS_NEW_USER", false)
        if (isNewUser) {
            navController.navigate(R.id.editProfileFragment)
        }
    }
}