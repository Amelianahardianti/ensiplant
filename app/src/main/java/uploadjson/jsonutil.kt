package com.example.ensiplant.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

//Buat baca file json dari asset
//gw tambahin firebaseutils belum tau kepake atau tidak

fun readJSONFromAssets(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

object FirebaseUtils {
    fun currentUser() = FirebaseAuth.getInstance().currentUser
    fun userDocRef() = FirebaseFirestore.getInstance().collection("users").document(currentUser()?.uid ?: "")
}
