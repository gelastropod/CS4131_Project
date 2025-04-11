package com.example.cs4131_project.model.firestoreModels

data class SavedItem(val izNotesItem: Boolean, val iz3d: Boolean, val notesItem: NotesItem?, val graphItem: GraphItem?, val graph3Item: Graph3Item?) {
    constructor(): this(false, false, null, null, null)

    fun isEmpty(): Boolean {
        return notesItem == null && graphItem == null && graph3Item == null
    }
}