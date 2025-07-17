package com.example.ensiplant.repository

import android.net.Uri
import android.util.Log
import com.example.ensiplant.model.forum.dataforumpost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class ForumRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun uploadPost(
        imageUri: Uri,
        caption: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("User not logged in")
        val timestamp = System.currentTimeMillis()
        val fileName = "${uid}_${timestamp}.jpg"
        val storageRef = storage.reference.child("post_images/$fileName")

        // 1. Upload gambar ke Storage
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val post = dataforumpost(
                        uid = uid,
                        username = username,
                        caption = caption,
                        imageUrl = downloadUrl.toString(),
                        timestamp = timestamp
                    )

                    // 2. Simpan ke Firestore
                    db.collection("posts")
                        .add(post)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError("Gagal menyimpan post: ${e.message}")
                        }

                }.addOnFailureListener {
                    onError("Gagal ambil imageUrl: ${it.message}")
                }
            }
            .addOnFailureListener {
                onError("Gagal upload gambar: ${it.message}")
            }
    }

    fun getAllPosts(): LiveData<List<dataforumpost>> {
        val postsLiveData = MutableLiveData<List<dataforumpost>>()

        db.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ForumRepository", "getAllPosts error: ${error.message}")
                    return@addSnapshotListener
                }

                val postList = mutableListOf<dataforumpost>()
                snapshot?.documents?.forEach { doc ->
                    val post = doc.toObject(dataforumpost::class.java)
                    if (post != null) {
                        postList.add(post.copy(id = doc.id)) // Inject ID
                    }
                }

                postsLiveData.value = postList
            }

        return postsLiveData
    }

    fun toggleLikePost(postId: String, uid: String) {
        val postRef = db.collection("posts").document(postId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val currentLikes = snapshot.get("likes") as? List<String> ?: emptyList()

            val newLikes = if (currentLikes.contains(uid)) {
                currentLikes - uid // unlike
            } else {
                currentLikes + uid // like
            }

            transaction.update(postRef, "likes", newLikes)
        }.addOnSuccessListener {
            Log.d("ForumRepository", "Like/unlike berhasil")
        }.addOnFailureListener {
            Log.e("ForumRepository", "Gagal like/unlike: ${it.message}")
        }
    }

}
