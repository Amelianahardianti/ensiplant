package com.example.ensiplant

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//VIEW MODEL LOGIC NYA UI BUAT AUTH

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val isAuthenticated = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        repository.login(email, password) {
            isAuthenticated.postValue(it)
        }
    }

    fun register(email: String, password: String, username: String) {
        repository.register(email, password, username) {
            isAuthenticated.postValue(it)
        }
    }

    fun signInWithGoogle(token: String) {
        repository.googleSignInWithFirebase(token) {
            isAuthenticated.postValue(it)
        }
    }

    fun logout() {
        repository.logout()
        isAuthenticated.postValue(false)
    }
}
