package com.example.ensiplant.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ensiplant.AuthRepository
import com.example.ensiplant.AuthViewModel
import com.example.ensiplant.AuthViewModelFactory
import com.example.ensiplant.MainActivity
import com.example.ensiplant.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val factory = AuthViewModelFactory(AuthRepository(auth, db))
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupActionListeners()
    }

    private fun setupActionListeners() {
        // Observe state dari ViewModel
        viewModel.isAuthenticated.observe(this) { isRegistered ->
            if (isRegistered) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Registrasi gagal. Coba lagi ya!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password dan konfirmasi tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jalankan proses register via ViewModel
            viewModel.register(email, password, username)
        }

        // Teks "Sudah punya akun?"
        binding.textViewToLogin.setOnClickListener {
            finish()
        }
    }

}