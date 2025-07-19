package com.example.ensiplant.ui.profile

import androidx.lifecycle.*
import com.example.ensiplant.User
import com.example.ensiplant.data.model.forum.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val _userPosts = MutableLiveData<List<Post>>()
    val userPosts: LiveData<List<Post>> get() = _userPosts

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let {
                    _userData.value = it
                }
            }
    }

    fun loadUserPosts() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("posts")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postList = querySnapshot.toObjects(Post::class.java)
                _userPosts.value = postList
            }
            .addOnFailureListener {
                _userPosts.value = emptyList()
            }
    }

    fun updateAvatar(avatarName: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .update("avatar", avatarName)
    }

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
