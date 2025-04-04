package com.example.cs4131_project.model.firestoreModels

data class UserAccount(val savedData: HashMap<String, SavedItem>, val password: String) {
    constructor() : this(hashMapOf(), "")
}