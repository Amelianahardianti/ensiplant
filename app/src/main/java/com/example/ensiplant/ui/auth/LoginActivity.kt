

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
import com.example.ensiplant.R
import com.example.ensiplant.databinding.ActivityLoginBinding
import com.example.ensiplant.ui.auth.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val factory = AuthViewModelFactory(AuthRepository(firebaseAuth, firestore))
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupActionListeners()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ambil dari google-services.json
            .requestEmail()
            .build()

        // GANTI requireContext() → this (karena ini Activity)
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.buttonGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        navigateToMain() // ✅ Jangan lupa ini
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionListeners() {
        viewModel.isAuthenticated.observe(this) { isLoggedIn ->
            showLoading(false)

            if (isLoggedIn) {
                val isChecked = binding.checkboxRememberMe.isChecked
                val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                prefs.edit().putBoolean("rememberMe", isChecked).apply()

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                val isChecked = binding.checkboxRememberMe.isChecked
                val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("rememberMe", isChecked)
                editor.apply()

                showLoading(true)
                viewModel.login(email, password)
            }
        }

        binding.textViewToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

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
