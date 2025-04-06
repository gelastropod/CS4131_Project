package com.example.cs4131_project.pages.dashboardPages

import android.provider.Settings.Global
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.MainActivity
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore

@Composable
fun SettingsPage(navController: NavController, mode: String, handler: FirestoreHandler) {
    val context = LocalContext.current
    var showDialog by remember{mutableStateOf(false)}
    var confettiEnabled by remember{ mutableStateOf(GlobalDatastore.confettiEnabled)}

    DashboardWrapper(navController, getString(context, R.string.settingsPageTitle), mode, handler = handler) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(getString(context, R.string.settingsPage1))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = MainActivity.darkThemeState.value,
                        onCheckedChange = {MainActivity.darkThemeState.value = it }
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(getString(context, R.string.settingsPage5))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = confettiEnabled,
                        onCheckedChange = {
                            confettiEnabled = it
                            GlobalDatastore.confettiEnabled = it
                            GlobalDatastore.updatePreferences()
                        }
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text(
                        getString(context, R.string.settingsPage2),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = getString(context, R.string.settingsPage3)) },
                        text = {
                            Column {
                                Text(text = getString(context, R.string.settingsPage4))
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false

                                    handler.data.remove(GlobalDatastore.username.value)
                                    for (className in handler.classData[GlobalDatastore.username.value] ?: arrayListOf()) {
                                        handler.data.remove(className)
                                    }
                                    handler.classData.remove(GlobalDatastore.username.value)
                                    handler.updateDatabase()

                                    GlobalDatastore.username.value = ""
                                    GlobalDatastore.updatePreferences()

                                    navController.navigate("homePage")
                                }
                            ) {
                                Text(getString(context, R.string.contentWrapper7))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog = false
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
            }
        }
    }
}