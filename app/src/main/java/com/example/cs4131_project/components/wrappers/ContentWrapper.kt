package com.example.cs4131_project.components.wrappers

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.utility.ExpandableFAB
import com.example.cs4131_project.components.utility.MiniFabItems
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.GraphViewModel
import com.google.gson.Gson
import java.io.OutputStream

fun writeDataToFile(context: Context, uri: Uri, data: String) {
    val contentResolver: ContentResolver = context.contentResolver
    val outputStream: OutputStream? = contentResolver.openOutputStream(uri)

    if (outputStream != null) {
        try {
            outputStream.write(data.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun ContentWrapper(navController: NavController, title: String, selectedState: Int = -1, floatingActionButton: @Composable (defaults: ArrayList<MiniFabItems>) -> Unit = {}, menuItems: @Composable ColumnScope.(expanded: MutableState<Boolean>) -> Unit = {}, mode: String, graphViewModel: GraphViewModel? = null, handler: FirestoreHandler, originalName: String, backRoute: String = "dashboardPage", content: @Composable () -> Unit) {
    val expandedState = remember {mutableStateOf(false)}
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(originalName) }
    val gson = Gson()

    val exportFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                val savedItem = handler.data[key]?.savedData?.get(originalName)!!

                val dataToWrite = gson.toJson(savedItem)

                writeDataToFile(context, it, dataToWrite)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            floatingActionButton(
                arrayListOf<MiniFabItems>().apply {
                    if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                        add(MiniFabItems(
                            painterResource(R.drawable.pencil), "Rename",
                            onClick = { showDialog = true }
                        ))
                        add(MiniFabItems(
                            painterResource(R.drawable.content_save), "Save",
                            onClick = {
                                handler.save()

                                Toast.makeText(
                                    context,
                                    getString(context, R.string.contentWrapper11),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ))
                        add(MiniFabItems(
                            painterResource(R.drawable.delete), "Delete",
                            onClick = { showDeleteDialog = true }
                        ))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        expandedState.value = false

                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TITLE, originalName)
                        }
                        exportFilePickerLauncher.launch(intent)
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.export),
                        contentDescription = "Overflow Menu"
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick = {
                        if (backRoute == "dashboardPage") {
                            if (handler.unsaved) {
                                showUnsavedDialog = true
                            } else {
                                if (GlobalDatastore.currentClass.value.isNotEmpty())
                                    navController.navigate("classDashboardPage/$mode/${GlobalDatastore.currentClass.value}")
                                else
                                    navController.navigate("${mode}DashboardPage")
                            }
                        }
                        else {
                            navController.navigate(backRoute)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "Back"
                    )
                }
            }
            HorizontalDivider()
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            if (showUnsavedDialog) {
                AlertDialog(
                    onDismissRequest = { showUnsavedDialog = false },
                    title = { Text(text = getString(context, R.string.dashboardWrapper5)) },
                    text = { Text(text = getString(context, R.string.dashboardWrapper6)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showUnsavedDialog = false

                            handler.unsaved = false
                            handler.unsavedData = handler.data

                            if (GlobalDatastore.currentClass.value.isNotEmpty())
                                navController.navigate("classDashboardPage/$mode/${GlobalDatastore.currentClass.value}")
                            else
                                navController.navigate("${mode}DashboardPage")
                        }) {
                            Text(getString(context, R.string.dashboardWrapper3))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showUnsavedDialog = false
                        }) {
                            Text(getString(context, R.string.dashboardWrapper4))
                        }
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                )
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = getString(context, R.string.contentWrapper15)) },
                    text = {
                        Column {
                            Text(text = getString(context, R.string.contentWrapper16))
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false

                                val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                                handler.data[key]?.savedData?.remove(originalName)
                                handler.updateDatabase()

                                if (GlobalDatastore.currentClass.value.isNotEmpty())
                                    navController.navigate("classDashboardPage/$mode/${GlobalDatastore.currentClass.value}")
                                else
                                    navController.navigate("${mode}DashboardPage")
                            }
                        ) {
                            Text(getString(context, R.string.contentWrapper7))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
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
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = getString(context, R.string.contentWrapper4)) },
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
                                handler.save()

                                handler.unsaved = true

                                val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                                val savedItem = handler.data[key]?.savedData?.get(originalName)
                                handler.unsavedData[key]?.savedData?.remove(originalName)

                                if (handler.unsavedData[key]?.savedData?.containsKey(name) == true) {
                                    Toast.makeText(context, getString(context, R.string.createNewPage9), Toast.LENGTH_SHORT).show()
                                    handler.unsavedData[key]?.savedData?.set(originalName, savedItem!!)
                                    return@TextButton
                                }

                                handler.unsavedData[key]?.savedData?.set(name, savedItem!!)

                                Toast.makeText(context, getString(context, R.string.contentWrapper12), Toast.LENGTH_SHORT)
                                    .show()
                                showDialog = false
                            },
                            enabled = name.isNotBlank()
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