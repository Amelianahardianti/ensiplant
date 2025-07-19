package com.example.ensiplant.ui.forum

import com.example.ensiplant.data.model.forum.Comment
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getComments(postId: String): Query {
        return db.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
    }

    suspend fun addComment(postId: String, comment: Comment): Boolean {
        return try {
            db.collection("posts")
                .document(postId)
                .collection("comments")
                .add(comment)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleLikeComment(postId: String, commentId: String, isLiked: Boolean, likeCount: Int): Boolean {
        return try {
            db.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .update(
                    mapOf(
                        "isLikedByUser" to isLiked,
                        "likeCount" to likeCount
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
