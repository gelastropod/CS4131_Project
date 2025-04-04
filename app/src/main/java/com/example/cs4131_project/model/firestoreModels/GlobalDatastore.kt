package com.example.cs4131_project.model.firestoreModels

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object GlobalDatastore {
    var sharedPreferences: SharedPreferences? = null
    var username = mutableStateOf("")

    fun updateUsername() {
        val savedUsername = sharedPreferences?.getString("username", "") ?: ""

        if (savedUsername != "")
            username.value = savedUsername
    }

    fun updatePreferences() {
        val editor = sharedPreferences?.edit()
        editor?.putString("username", username.value)
        editor?.apply()
    }
}