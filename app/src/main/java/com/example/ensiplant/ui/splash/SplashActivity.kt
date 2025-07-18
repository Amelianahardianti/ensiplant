package com.example.ensiplant.ui.splash

import LoginActivity
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.ensiplant.MainActivity
import com.example.ensiplant.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashScreenDuration = 2000L // 2 detik

        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, splashScreenDuration)
    }

    private fun checkLoginStatus() {
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val rememberMe = prefs.getBoolean("rememberMe", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (rememberMe && currentUser != null) {
            // ✅ Sudah login dan remember me aktif
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // ⛔ Belum login atau rememberMe tidak dicentang
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
