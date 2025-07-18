//ini buat ngatur file user

package com.example.ensiplant.repository

import com.example.ensiplant.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun getUserData(uid: String, onComplete: (User) -> Unit) {
        usersCollection.document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    onComplete(user)
                }
            }
    }
}

