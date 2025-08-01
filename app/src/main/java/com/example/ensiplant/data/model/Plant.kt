package com.example.ensiplant.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//buat bikin data (sesuai dengan json)

@Parcelize
data class Plant(
    val id: String = "",
    val nama: String = "",
    val foto: String? = null,
    val latin: String = "",
    val timestamp: String = "",
    val deskripsi: String = ""
) : Parcelable

