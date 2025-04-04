package com.example.cs4131_project.pages.dashboardPages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.GraphItem
import com.example.cs4131_project.model.firestoreModels.NotesItem
import com.example.cs4131_project.model.firestoreModels.SavedItem

@Composable
fun CreateNewPage(navController: NavController, mode: String, handler: FirestoreHandler) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(0) }

    DashboardWrapper(navController, getString(context, R.string.createNewPageTitle), mode) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onClick = {
                        showDialog = 1
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chart_bell_curve_cumulative),
                            contentDescription = "Graph",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                        Text(
                            text = getString(context, R.string.createNewPage1),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onClick = {
                        showDialog = 2
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.note),
                            contentDescription = "Notes",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                        Text(
                            text = getString(context, R.string.createNewPage2),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text(getString(context, R.string.createNewPage8))
            }
        }
    }
    if (showDialog != 0) {
        AlertDialog(
            onDismissRequest = { showDialog = 0 },
            title = {
                Text(
                    text = getString(
                        context,
                        R.string.createNewPage3
                    ) + if (showDialog == 1) "graph" else "notes"
                )
            },
            text = {
                Column {
                    Text(text = getString(context, R.string.createNewPage4))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        placeholder = { Text(getString(context, R.string.createNewPage5)) }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (showDialog == 1) {
                            handler.unsavedData[GlobalDatastore.username.value]?.savedData?.set(name, SavedItem(false, null, GraphItem()))
                        }
                        else {
                            handler.unsavedData[GlobalDatastore.username.value]?.savedData?.set(name, SavedItem(true, NotesItem(), null))
                        }

                        handler.unsaved = true

                        if (showDialog == 1) {
                            navController.navigate("graphPage/$mode/$name")
                        }
                        else {
                            navController.navigate("notesPage/$mode//$name")
                        }
                        showDialog = 0
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text(getString(context, R.string.createNewPage6))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = 0
                }) {
                    Text(getString(context, R.string.createNewPage7))
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}