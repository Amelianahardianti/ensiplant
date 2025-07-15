package com.example.ensiplant.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        // Observe state dari ViewModel
        viewModel.isAuthenticated.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Login gagal! Periksa kembali email dan password kamu.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Tombol Login
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.login(email, password)
            }
        }

        // Teks "Sign Up"
        binding.textViewToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
