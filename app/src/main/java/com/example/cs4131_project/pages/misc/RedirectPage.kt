package com.example.cs4131_project.pages.misc

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.SavedItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun RedirectPage(navController: NavController, originalName: String, fileContent: String, handler: FirestoreHandler) {
    val context = LocalContext.current
    var name by remember{mutableStateOf(originalName)}
    val gson = Gson()
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = getString(context, R.string.contentWrapper15)) },
        text = {
            Column {
                Text(text = getString(context, R.string.contentWrapper5))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text(getString(context, R.string.contentWrapper6)) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val savedItem: SavedItem = gson.fromJson(
                            fileContent,
                            object : TypeToken<SavedItem>() {}.type
                        )

                        val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                        handler.data[key]?.savedData?.set(
                            name,
                            savedItem
                        )
                        handler.updateDatabase()

                        navController.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            getString(context, R.string.dashboardWrapper14),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(getString(context, R.string.contentWrapper7))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text(getString(context, R.string.contentWrapper8))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}