package com.example.ensiplant.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ensiplant.MainActivity
import com.example.ensiplant.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupActionListeners()
    }

    private fun setupActionListeners() {
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
                Toast.makeText(this, "Password dan konfirmasi password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jika validasi lolos, lanjutkan dengan registrasi ke Firebase
            // 3. Panggil fungsi createUserWithEmailAndPassword
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Jika registrasi berhasil
                        Log.d("Auth", "createUserWithEmail:success")
                        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                        finish()
                    } else {
                        // Jika registrasi gagal
                        Log.w("Auth", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Registrasi gagal: ${task.exception?.message}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
        }

        binding.textViewToLogin.setOnClickListener {
            finish()
        }
    }
}