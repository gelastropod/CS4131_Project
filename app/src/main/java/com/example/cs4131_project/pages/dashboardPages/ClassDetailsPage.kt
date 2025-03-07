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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.DashboardWrapper

@Composable
fun ClassDetailsPage(navController: NavController, mode: String) {
    val context = LocalContext.current

    DashboardWrapper(navController, getString(context, R.string.classDetailsPageTitle), mode) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Class ID copied to clipboard!", Toast.LENGTH_SHORT)
                        .show()
                }
            ) {
                Text("Copy Class ID")
            }
            Button(
                onClick = {
                    navController.navigate("classDashboardPage/$mode")
                }
            ) {
                Text("Show class resources")
            }
        }
    }
}