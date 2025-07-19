package com.example.ensiplant.data.model.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userAvatarUrl: String,
    val text: String,
    val timestamp: Long,
    var isEdited: Boolean = false,

    var likeCount: Int,
    var isLikedByUser: Boolean = false
) : Parcelable