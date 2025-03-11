package com.example.cs4131_project.components.wrappers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import kotlinx.coroutines.launch

@Composable
fun DashboardWrapper(
    navController: NavController,
    title: String,
    mode: String,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogOutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = "Graphium",
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = false,
                    onClick = {
                        navController.navigate("${mode}DashboardPage")
                    },
                    shape = RectangleShape
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate("settingsPage/$mode")
                    },
                    shape = RectangleShape
                )
                if (mode != "personal") {
                    NavigationDrawerItem(
                        label = { Text("Classes") },
                        selected = false,
                        onClick = {
                            navController.navigate(mode + "ClassListPage")
                        },
                        shape = RectangleShape
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    selected = false,
                    onClick = {
                        showLogOutDialog = true
                    },
                    shape = RectangleShape
                )
                NavigationDrawerItem(
                    label = { Text("Import...") },
                    selected = false,
                    onClick = { },
                    shape = RectangleShape
                )
                NavigationDrawerItem(
                    label = { Text("Export...") },
                    selected = false,
                    onClick = { },
                    shape = RectangleShape
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(10.dp),
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
                modifier = Modifier.padding(16.dp),
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