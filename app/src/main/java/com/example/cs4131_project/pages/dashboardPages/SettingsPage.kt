package com.example.cs4131_project.pages.dashboardPages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.MainActivity
import com.example.cs4131_project.R
import com.example.cs4131_project.components.DashboardWrapper

@Composable
fun SettingsPage(navController: NavController, mode: String) {
    val context = LocalContext.current

    DashboardWrapper(navController, getString(context, R.string.settingsPageTitle), mode) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Button(
                    onClick = {
                        MainActivity.darkThemeState.value = !MainActivity.darkTheme
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (MainActivity.darkTheme) {
                        Text("Switch to light mode")
                    } else {
                        Text("Switch to dark mode")
                    }
                }
            }
        }
    }
}