package com.example.ensiplant.data.repository

import com.example.ensiplant.data.model.forum.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//ngatur forum di firebase nya kaya diambil terus dibuat lalu like nya gitu gitu

class PostRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postCollection = db.collection("posts")


    suspend fun createPost(post: Post): Boolean {
        return try {
            postCollection.document(post.id).set(post).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    suspend fun toggleLike(postId: String, userId: String) {
        val postRef = postCollection.document(postId)
        val snapshot = postRef.get().await()

        val likedBy = snapshot["likedBy"] as? List<String> ?: listOf()

        if (likedBy.contains(userId)) {
            // Unlike
            postRef.update(
                "likedBy", FieldValue.arrayRemove(userId),
                "likeCount", FieldValue.increment(-1)
            )
        } else {
            // Like
            postRef.update(
                "likedBy", FieldValue.arrayUnion(userId),
                "likeCount", FieldValue.increment(1)
            )
        }
    }

}
