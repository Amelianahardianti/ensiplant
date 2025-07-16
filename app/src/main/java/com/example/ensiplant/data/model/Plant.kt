package com.example.ensiplant.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//buat bikin data (sesuai dengan json)

@Parcelize
data class Plant(
    val id: String,
    val nama: String,
    val foto: String,
    val latin: String,
    val timestamp: String
) : Parcelable
