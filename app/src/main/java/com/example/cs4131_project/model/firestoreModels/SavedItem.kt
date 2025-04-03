package com.example.cs4131_project.model.firestoreModels

data class SavedItem(val izNotesItem: Boolean, val notesItem: NotesItem?, val graphItem: GraphItem?) {
    constructor(): this(false, null, null)

    fun isEmpty(): Boolean {
        return notesItem == null && graphItem == null
    }
}