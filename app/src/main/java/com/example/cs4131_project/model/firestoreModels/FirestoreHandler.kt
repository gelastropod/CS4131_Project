package com.example.cs4131_project.model.firestoreModels

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FirestoreHandler(private val documentReference: DocumentReference) {
    companion object {
        val gson = Gson()
    }

    var data: HashMap<String, UserAccount> = hashMapOf()
    var unsavedData: HashMap<String, UserAccount> = hashMapOf()
    var unsaved = false

    fun updateData(onSuccess: (() -> Unit) = {}) {
        documentReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val json = gson.toJson(document.data)

                data = gson.fromJson(
                    json,
                    object : TypeToken<HashMap<String, UserAccount>>() {}.type
                )

                unsavedData = data
                unsaved = false

                onSuccess()

                Log.i("Firestore", "Successfully retrieved data!")
            }
            else {
                Log.e("Firestore", "Document does not exist!")
            }
        }
        .addOnFailureListener {
            Log.e("Firestore", "Error getting document: ${it.message}")
        }
    }

    fun save() {
        if (!unsaved) return
        data = unsavedData
        updateDatabase()
        unsaved = false
    }

    fun updateDatabase() {
        val json = gson.toJson(data)
        Log.i("AAA", json)
        documentReference.set(data)
            .addOnSuccessListener {
                Log.i("Firestore", "Database updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating database: ${it.message}")
            }
    }

    init {
        updateData {
            GlobalDatastore.updateUsername()
        }
    }
}