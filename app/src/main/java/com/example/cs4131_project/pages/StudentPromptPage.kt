package com.example.cs4131_project.pages

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.NoToolbarWrapper.Companion.NoToolbarWrapper

class StudentPromptPage {
    companion object {
        @Composable
        fun StudentPromptPage(navController: NavController) {
            val context = LocalContext.current

            NoToolbarWrapper(navController, getString(context, R.string.studentPromptPageTitle)) {
                Text(
                    text = getString(context, R.string.studentPromptPage1),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }
}