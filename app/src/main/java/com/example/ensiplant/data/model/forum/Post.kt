package com.example.ensiplant.data.model.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatarUrl: String, // URL untuk foto profil pembuat post
    val postDate: String,
    val postImageUrl: String, // URL untuk gambar tanaman yang di-post
    val caption: String,
    var likeCount: Int,
    var commentCount: Int,
    var isLikedByUser: Boolean = false // Untuk menandai apakah user ini sudah like post ini
) : Parcelable