package com.example.ensiplant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import android.util.Log
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = Firebase.auth.currentUser
        Log.d("FIREBASE", "User Login: $user")

        uploadPlantsToFirestore(this)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            Log.d("AuthCheck", "User is logged in: ${currentUser.email}")
        } else {
            Log.d("AuthCheck", "No user is logged in")
        }


    }


}