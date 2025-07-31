package com.example.ensiplant.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ensiplant.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val factory = AuthViewModelFactory(AuthRepository(auth, db))
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupActionListeners()
    }

    private fun setupActionListeners() {
        viewModel.isAuthenticated.observe(this) { isRegistered ->
            // Loading di sembunyikan setelah proses selesai
            showLoading(false)

            if (isRegistered) {
                Toast.makeText(this, "Registration successful! Please login..", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Register
        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsernameRegister.text.toString().trim()
            val email = binding.editTextEmailRegister.text.toString().trim()
            val password = binding.editTextPasswordRegister.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPasswordRegister.text.toString().trim()

            // Validasi input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password and confirmation do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true)
            viewModel.register(email, password, username)
        }

        binding.textViewToLogin.setOnClickListener {
            finish()
        }
    }

    // Menampilkan/menyembunyikan ProgressBar
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarRegister.visibility = View.VISIBLE
            binding.buttonRegister.isEnabled = false
        } else {
            binding.progressBarRegister.visibility = View.GONE
            binding.buttonRegister.isEnabled = true
        }
    }
}
