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
        // Mengamati perubahan state registrasi dari ViewModel
        viewModel.isAuthenticated.observe(this) { isRegistered ->
            showLoading(false)

            if (isRegistered) {
                Toast.makeText(this, "Registrasi berhasil! Silakan lengkapi profilmu.", Toast.LENGTH_LONG).show()

                // Pindah ke MainActivity dengan membawa sinyal "pengguna baru"
                val intent = Intent(this, MainActivity::class.java).apply {
                    // Menambahkan "pesan" bahwa ini adalah pengguna baru
                    putExtra("IS_NEW_USER", true)
                    // Membersihkan semua activity sebelumnya agar tidak bisa kembali ke login/register
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)

            } else {
                Toast.makeText(this, "Registrasi gagal. Email mungkin sudah terdaftar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Register
        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsernameRegister.text.toString().trim()
            val email = binding.editTextEmailRegister.text.toString().trim()
            val password = binding.editTextPasswordRegister.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPasswordRegister.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password dan konfirmasi tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true)
            viewModel.register(email, password, username)
        }

        // Teks "Sudah punya akun?" untuk kembali ke Login
        binding.textViewToLogin.setOnClickListener {
            finish()
        }
    }

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
