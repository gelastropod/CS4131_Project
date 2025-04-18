package com.example.cs4131_project.pages.dashboardPages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore

@Composable
fun JoinClassPage(navController: NavController, handler: FirestoreHandler) {
    val context = LocalContext.current
    var className by remember { mutableStateOf("") }

    DashboardWrapper(navController, getString(context, R.string.joinClassPageTitle), "student", handler = handler) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getString(context, R.string.joinClassPage1),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(0.9f),
                textAlign = TextAlign.Center
            )
            TextField(
                value = className,
                onValueChange = { newText -> className = newText },
                placeholder = { Text(getString(context, R.string.joinClassPage2)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        for (item in handler.data) {
                            if (item.value.password == className) {
                                GlobalDatastore.currentClass.value = item.key

                                handler.classData[GlobalDatastore.username.value]?.add(item.key)
                                handler.updateDatabase()

                                navController.navigate("classDashboardPage/student/${item.key}")

                                return@Button
                            }
                        }

                        Toast.makeText(context, getString(context, R.string.joinClassPage4), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(getString(context, R.string.joinClassPage3))
                }
            }
        }
    }
}