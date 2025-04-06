package com.example.cs4131_project.pages.dashboardPages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore

@Composable
fun ClassDetailsPage(navController: NavController, mode: String, handler: FirestoreHandler, className: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    DashboardWrapper(navController, className, mode, handler = handler) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(handler.data[className]?.password ?: ""))

                    Toast.makeText(context, "Class ID copied to clipboard!", Toast.LENGTH_SHORT)
                        .show()
                }
            ) {
                Text("Copy Class ID")
            }
            Button(
                onClick = {
                    GlobalDatastore.currentClass.value = className

                    navController.navigate("classDashboardPage/$mode/$className")
                }
            ) {
                Text("Show class resources")
            }
        }
    }
}