package com.example.cs4131_project.pages.contentPages

import android.graphics.drawable.Icon
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun NotesPage(navController: NavController, mode: String, notesContent: String, handler: FirestoreHandler, name: String) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(notesContent) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isReading by remember { mutableStateOf(true)}

    ContentWrapper(
        navController,
        getString(context, R.string.notesPageTitle),
        mode = mode,
        handler = handler,
        originalName = name,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isReading = !isReading
                }
            ) {
                Icon(
                     painterResource(
                        if (isReading)
                            R.drawable.pencil
                        else
                            R.drawable.eye,
                    ),
                    contentDescription = "Thingy"
                )
            }
        }
    ) {
        if (!isReading) {
            TextField(
                value = text,
                onValueChange = {
                    text = it

                    handler.unsaved = true

                    handler.unsavedData[GlobalDatastore.username.value]?.savedData?.get(name)?.notesItem?.notesContent =
                        it
                },
                modifier = Modifier.fillMaxSize(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )
        }
        else {
            Card(
                modifier = Modifier.fillMaxSize()
            ) {
                MarkdownText(
                    markdown = text,
                    modifier = Modifier.padding(10.dp),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                )
            }
        }
    }
}