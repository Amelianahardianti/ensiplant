package com.example.ensiplant.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ensiplant.AuthRepository
import com.example.ensiplant.AuthViewModel
import com.example.ensiplant.AuthViewModelFactory
import com.example.ensiplant.MainActivity
import com.example.ensiplant.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val factory = AuthViewModelFactory(AuthRepository(firebaseAuth, firestore))
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupActionListeners()
    }

    private fun setupActionListeners() {
        viewModel.isAuthenticated.observe(this) { isLoggedIn ->
            showLoading(false) // Loading disembunyikan setelah proses selesai

            if (isLoggedIn) {
                // Jika true
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                //
            }
        }

        // Tombol Login Email/Password
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                viewModel.login(email, password)
            }
        }

        // Teks "Sign Up"
        binding.textViewToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Menampilkan/menyembunyikan ProgressBar
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonLogin.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.buttonLogin.isEnabled = true
        }
    }
}