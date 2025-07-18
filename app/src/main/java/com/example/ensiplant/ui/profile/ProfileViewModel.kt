package com.example.ensiplant.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ensiplant.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Load user dari Firestore
    fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        _userData.value = user
                    }
                }
            }
    }


    // Update avatar secara terpisah (opsional)
    fun updateAvatar(avatarName: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .update("avatar", avatarName)
    }

    // Update semua data (username, city, avatar)
    fun updateUserProfile(
        username: String,
        location: String,
        avatar: String,
        callback: (Boolean) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return callback(false)

        val updatedData = mapOf(
            "username" to username,
            "location" to location,
            "avatar" to avatar
        )

        firestore.collection("users").document(uid)
            .update(updatedData)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}
