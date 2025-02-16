package com.example.cs4131_project.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.NoToolbarWrapper.Companion.NoToolbarWrapper
import kotlinx.coroutines.launch

class StudentPromptPage {
    companion object {
        @Composable
        fun StudentPromptPage(navController: NavController) {
            val context = LocalContext.current
            var className by remember {mutableStateOf("")}

            NoToolbarWrapper(navController, getString(context, R.string.studentPromptPageTitle)) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = getString(context, R.string.studentPromptPage1),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = className,
                    onValueChange = { newText -> className = newText },
                    placeholder = { Text(getString(context, R.string.studentPromptPage2)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            navController.navigate("studentDashboardPage")
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text(getString(context, R.string.studentPromptPage3))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            navController.navigate("studentDashboardPage")
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text(getString(context, R.string.studentPromptPage4))
                    }
                }
            }
        }
    }
}