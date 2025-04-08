package com.example.cs4131_project.pages.dashboardPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun InformationPage(navController: NavController, mode: String, handler: FirestoreHandler) {
    val context = LocalContext.current

    DashboardWrapper(navController, getString(context, R.string.informationPageTitle), mode, handler) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "App Icon"
                    )
                }
            }
            item {
                MarkdownText(
                    markdown = getString(context, R.string.informationPage1),
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                )
            }
            item {
                MarkdownText(
                    markdown = getString(context, R.string.informationPage2),
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                )
            }
        }
    }
}