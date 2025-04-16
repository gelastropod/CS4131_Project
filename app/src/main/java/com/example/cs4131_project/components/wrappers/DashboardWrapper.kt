package com.example.cs4131_project.components.wrappers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler.Companion
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.SavedItem
import com.example.cs4131_project.model.firestoreModels.UserAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun FilePickerDemo() {
    val context = LocalContext.current
    var fileContent by remember { mutableStateOf("No file selected") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                fileContent = readFileContent(context, it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            filePickerLauncher.launch(intent)
        }) {
            Text("Choose File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("File Content:\n$fileContent")
    }
}

fun readFileContent(context: android.content.Context, uri: Uri): String {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).readText()
    } ?: "Failed to read file"
}

fun getFileName(context: Context, uri: Uri): String {
    var fileName = "unknown"

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1) {
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
    }

    return fileName
}

@Composable
fun DashboardWrapper(
    navController: NavController,
    title: String,
    mode: String,
    handler: FirestoreHandler,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogOutDialog by remember { mutableStateOf(false) }
    val route = navController.currentBackStackEntry?.destination?.route ?: ""

    val importFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val fileContent = readFileContent(context, it)
                Log.e("AAA", fileContent)
                var fileName = getFileName(context, it)
                if (fileName.endsWith(".txt"))
                    fileName = fileName.substring(0, fileName.length - 4)

                val encodedContent = Uri.encode(fileContent)
                navController.navigate("redirectPage/$fileName/$encodedContent")
            }
        }
    }

    ModalNavigationDrawer(
        gesturesEnabled = GlobalDatastore.gesturesEnabled.value,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "funny"
                )
                Text(
                    text = getString(context, R.string.dashboardWrapper7),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.displaySmall
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.view_dashboard),
                            contentDescription = "dashboard"
                        )
                    },
                    label = { Text(getString(context, R.string.dashboardWrapper8)) },
                    selected = false,
                    onClick = {
                        GlobalDatastore.currentClass.value = ""

                        if (!route.startsWith("${mode}DashboardPage"))
                            navController.navigate("${mode}DashboardPage")
                    },
                    shape = RectangleShape
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.cog),
                            contentDescription = "dashboard"
                        )
                    },
                    label = { Text(getString(context, R.string.dashboardWrapper9)) },
                    selected = false,
                    onClick = {
                        GlobalDatastore.currentClass.value = ""

                        if (!route.startsWith("settingsPage"))
                            navController.navigate("settingsPage/$mode")
                    },
                    shape = RectangleShape
                )
                if (mode != "personal") {
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.google_classroom),
                                contentDescription = "dashboard"
                            )
                        },
                        label = { Text(getString(context, R.string.dashboardWrapper10)) },
                        selected = false,
                        onClick = {
                            GlobalDatastore.currentClass.value = ""

                            if (!route.startsWith("${mode}ClassListPage"))
                                navController.navigate(mode + "ClassListPage")
                        },
                        shape = RectangleShape
                    )
                }
                if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.import_icon),
                                contentDescription = "dashboard"
                            )
                        },
                        label = { Text(getString(context, R.string.dashboardWrapper12)) },
                        selected = false,
                        onClick = {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "text/plain"
                            }
                            importFilePickerLauncher.launch(intent)

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        shape = RectangleShape
                    )
                }
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.information),
                            contentDescription = "dashboard"
                        )
                    },
                    label = {Text(getString(context, R.string.dashboardWrapper15))},
                    selected = false,
                    onClick = {
                        GlobalDatastore.currentClass.value = ""

                        if (!route.startsWith("informationPage"))
                            navController.navigate("informationPage/$mode")
                    },
                    shape = RectangleShape
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = "dashboard"
                        )
                    },
                    label = { Text(getString(context, R.string.dashboardWrapper11)) },
                    selected = false,
                    onClick = {
                        showLogOutDialog = true
                    },
                    shape = RectangleShape
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Menu"
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            HorizontalDivider()
            Box(
                modifier = Modifier.padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            if (showLogOutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogOutDialog = false },
                    title = { Text(text = getString(context, R.string.dashboardWrapper1)) },
                    text = { Text(text = getString(context, R.string.dashboardWrapper2)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogOutDialog = false

                            GlobalDatastore.username.value = ""
                            GlobalDatastore.updatePreferences()

                            GlobalDatastore.currentClass.value = ""
                            GlobalDatastore.confettiShown = false

                            navController.navigate("homePage")
                        }) {
                            Text(getString(context, R.string.dashboardWrapper3))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showLogOutDialog = false
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
        }
    }
}