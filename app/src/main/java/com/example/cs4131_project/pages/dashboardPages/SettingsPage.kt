package com.example.cs4131_project.pages.dashboardPages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.MainActivity
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper

@Composable
fun SettingsPage(navController: NavController, mode: String) {
    val context = LocalContext.current

    DashboardWrapper(navController, getString(context, R.string.settingsPageTitle), mode) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(getString(context, R.string.settingsPage1))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = MainActivity.darkThemeState.value,
                        onCheckedChange = {MainActivity.darkThemeState.value = it }
                    )
                }
            }
        }
    }
}