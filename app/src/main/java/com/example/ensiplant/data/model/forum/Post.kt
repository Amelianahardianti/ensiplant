package com.example.ensiplant.data.model.forum

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Post(
    val id: String = "",
    val uid: String = "",
    val username: String = "",
    val avatar: String = "",
    val postDate: String = "",
    val postImageUrl: String? = null,
    val caption: String = "",
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    val isVideo: Boolean = false,

    var likes: List<String> = emptyList(), // ⬅️ INI AJA YANG DIPAKAI

    @get:Exclude var isLikedByUser: Boolean = false // ⬅️ UNTUK UI AJA, JANGAN DISIMPAN
) : Parcelable

