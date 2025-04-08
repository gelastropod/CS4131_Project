package com.example.cs4131_project.model.firestoreModels

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object GlobalDatastore {
    var sharedPreferences: SharedPreferences? = null
    var username = mutableStateOf("")
    var currentClass = mutableStateOf("")
    var gesturesEnabled = mutableStateOf(true)
    var confettiEnabled = true
    var confettiShown = false

    fun updateData() {
        val savedUsername = sharedPreferences?.getString("username", "") ?: ""
        confettiEnabled = sharedPreferences?.getBoolean("confettiEnabled", true) ?: true
        gesturesEnabled.value = sharedPreferences?.getBoolean("gesturesEnabled", true) ?: true

        if (savedUsername != "")
            username.value = savedUsername
    }

    fun updatePreferences() {
        val editor = sharedPreferences?.edit()
        editor?.putString("username", username.value)
        editor?.putBoolean("confettiEnabled", confettiEnabled)
        editor?.putBoolean("gesturesEnabled", gesturesEnabled.value)
        editor?.apply()
    }
}