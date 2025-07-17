package com.example.ensiplant.model.forum

data class dataforumpost(
    val id: String = "", //id post nya
    val uid: String = "", //id si user nge postnya
    val username: String = "", //username yang nge post
    val caption: String = "", // isi postingannya
    val imageUrl: String = "", // gambar nya kalau misalnya ga ada null aja
    val timestamp: Long = 0L, // waktu ngepost
    val likes: List<String> = emptyList(), //yang like
)

data class Comment(
    val id: String = "",  //id komen buat user yang komen
    val uid: String = "", //id user yang komen
    val username: String = "", //username yang komen
    val comment: String = "", //isi komentar
    val timestamp: Long = 0L // waktu komen
)

