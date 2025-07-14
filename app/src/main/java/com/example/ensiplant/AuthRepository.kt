package com.example.ensiplant

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {

    fun register(email: String, password: String, username: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val user = hashMapOf("email" to email, "username" to username)
                    if (uid != null) {
                        db.collection("users").document(password).set(user) //ntr namanya users di auth
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    }
                } else {
                    onResult(false)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    fun googleSignInWithFirebase(token: String, onResult: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Optional: Save to Firestore if new
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}
