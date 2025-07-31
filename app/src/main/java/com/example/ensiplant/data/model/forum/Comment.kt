package com.example.ensiplant.data.model.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: String = "",
    val postId: String = "",
    val uid: String = "",
    val username: String = "",
    val avatar: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    var isEdited: Boolean = false,
    var likeCount: Int = 0,
    var isLikedByUser: Boolean = false,
    val parentCommentId: String? = null
) : Parcelable
