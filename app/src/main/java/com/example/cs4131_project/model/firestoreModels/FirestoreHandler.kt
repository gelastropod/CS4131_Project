package com.example.cs4131_project.model.firestoreModels

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FirestoreHandler(private val documentReference: DocumentReference, private val classDocumentReference: DocumentReference, private val classIDDocumentReference: DocumentReference) {
    companion object {
        val gson = Gson()
    }

    var data: HashMap<String, UserAccount> = hashMapOf()
    var classData: HashMap<String, ArrayList<String>> = hashMapOf()
    var classIDData: HashMap<String, String> = hashMapOf()
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

        classDocumentReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val json = gson.toJson(document.data)

                classData = gson.fromJson(
                    json,
                    object : TypeToken<HashMap<String, ArrayList<String>>>() {}.type
                )

                Log.i("Firestore", "Successfully retrieved data!")
            } else {
                Log.e("Firestore", "Document does not exist!")
            }
        }
        .addOnFailureListener {
            Log.e("Firestore", "Error getting document: ${it.message}")
        }

        classIDDocumentReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val json = gson.toJson(document.data)

                classIDData = gson.fromJson(
                    json,
                    object : TypeToken<HashMap<String, String>>() {}.type
                )

                Log.i("Firestore", "Successfully retrieved data!")
            } else {
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
        documentReference.set(data)
            .addOnSuccessListener {
                Log.i("Firestore", "Database updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating database: ${it.message}")
            }

        classDocumentReference.set(classData)
            .addOnSuccessListener {
                Log.i("Firestore", "Database updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating database: ${it.message}")
            }

        classIDDocumentReference.set(classIDData)
            .addOnSuccessListener {
                Log.i("Firestore", "Database updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating database: ${it.message}")
            }
    }

    init {
        updateData {
            GlobalDatastore.updateData()
        }
    }
}