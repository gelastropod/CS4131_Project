package com.example.cs4131_project.pages.contentPages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore

@Composable
fun NotesPage(navController: NavController, mode: String, notesContent: String, handler: FirestoreHandler, name: String) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(notesContent) }
    val keyboardController = LocalSoftwareKeyboardController.current

    ContentWrapper(
        navController,
        getString(context, R.string.notesPageTitle),
        mode = mode,
        handler = handler,
        originalName = name
    ) {
        TextField(
            value = text,
            onValueChange = {
                text = it

                handler.unsaved = true

                handler.unsavedData[GlobalDatastore.username.value]?.savedData?.get(name)?.notesItem?.notesContent = it
            },
            modifier = Modifier.fillMaxSize(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )
    }
}