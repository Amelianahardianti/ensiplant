package com.example.ensiplant.model.forumcomment

data class Comment(
    val id: String = "",  //id komen buat user yang komen
    val uid: String = "", //id user yang komen
    val username: String = "", //username yang komen
    val comment: String = "", //isi komentar
    val timestamp: Long = 0L // waktu komen
)
