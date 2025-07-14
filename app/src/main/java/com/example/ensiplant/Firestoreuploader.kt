package com.example.ensiplant

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ensiplant.model.Dataplant
import com.example.ensiplant.utils.readJSONFromAssets

//buat upload filejson nya ke firestore cloud ges
//buat upload data forum postingan user ke firestore cloud dan storage


fun uploadPlantsToFirestore(context: Context) {
    val jsonString = readJSONFromAssets(context,"plantdum.json") //nama file json di asset
    val gson = Gson()

    val plantList: List<Dataplant> = gson.fromJson(jsonString, object : TypeToken<List<Dataplant>>() {}.type) //list data di data plant file kt dan file itu sama dengan file json datanya

    val db = FirebaseFirestore.getInstance() // masukin ke firestore cloud

    for (plant in plantList) {
        db.collection("plants") // nama di firestore cloud nya ntr kaya folder
            .document(plant.id)  // ID langsung pakai dari JSON
            .set(plant)
            .addOnSuccessListener { //buat pertanda di logcat
                Log.d("UPLOAD", "Uploaded: ${plant.nama}")
            }
            .addOnFailureListener { e ->
                Log.e("UPLOAD", "Error uploading ${plant.nama}", e)
            }
    }
}




