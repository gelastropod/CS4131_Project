package com.example.cs4131_project.components.wrappers

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun ContentWrapper(navController: NavController, title: String, selectedState: Int = -1, floatingActionButton: @Composable () -> Unit = {}, mode: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false)}
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = floatingActionButton
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
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = if (expanded) R.drawable.menu_up else R.drawable.menu_down),
                        contentDescription = "Overflow Menu"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = (-300).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(getString(context, R.string.contentWrapper1)) },
                        onClick = {
                            expanded = false
                            navController.navigate("${mode}DashboardPage")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(getString(context, R.string.contentWrapper2)) },
                        onClick = {
                            expanded = false
                            navController.navigate("${mode}DashboardPage")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(getString(context, R.string.contentWrapper3)) },
                        onClick = {
                            expanded = false
                            showDialog = true
                        }
                    )
                    if (selectedState != -1) {
                        DropdownMenuItem(
                            text = { Text(getString(context, R.string.contentWrapper9)) },
                            onClick = {
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(getString(context, R.string.contentWrapper10)) },
                            onClick = {
                                expanded = false
                            }
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
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
                                showDialog = false
                                Toast.makeText(context, "Renamed successfully", Toast.LENGTH_SHORT)
                                    .show()
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