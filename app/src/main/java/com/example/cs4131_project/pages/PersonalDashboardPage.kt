package com.example.cs4131_project.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.DashboardWrapper.Companion.DashboardWrapper

class PersonalDashboardPage {
    companion object {
        @Composable
        fun PersonalDashboardPage(navController: NavController) {
            val context = LocalContext.current

            DashboardWrapper(navController, getString(context, R.string.personalDashboardPageTitle)) {
                Text("A")
            }
        }
    }
}