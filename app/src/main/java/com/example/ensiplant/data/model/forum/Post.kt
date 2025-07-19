package com.example.ensiplant.data.model.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    val uid: String = "",
    val username: String = "",
    val avatar: String = "", // URL untuk foto profil pembuat post
    val postDate: String = "",
    val postImageUrl: String? = null, // URL gambar tanaman (boleh null)
    val caption: String = "",
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    var isLikedByUser: Boolean = false,
    val isVideo: Boolean = false // Tandai apakah ini video (jika nanti fitur ini dipakai)
) : Parcelable
