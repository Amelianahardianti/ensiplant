package com.example.ensiplant.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun register(email: String, password: String, username: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val user = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "username" to username,
                        "location" to null,
                        "avatar" to null
                    )
                    if (uid != null) {
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    } else {
                        onResult(false)
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
                    val uid = user?.uid
                    val email = user?.email ?: ""
                    val username = user?.displayName ?: ""

                    // Simpan user baru ke Firestore kalau belum ada
                    if (uid != null) {
                        val userRef = db.collection("users").document(uid)
                        userRef.get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                val userData = hashMapOf(
                                    "uid" to uid,
                                    "email" to email,
                                    "username" to username,
                                    "location" to null,
                                    "avatar" to null
                                )
                                userRef.set(userData)
                            }
                        }
                    }

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
