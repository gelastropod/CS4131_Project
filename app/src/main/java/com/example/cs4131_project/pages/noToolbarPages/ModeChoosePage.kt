package com.example.cs4131_project.pages.noToolbarPages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.NoToolbarWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.UserAccount

@Composable
fun ModeChoosePage(navController: NavController, handler: FirestoreHandler, username: String, password: String) {
    val context = LocalContext.current

    NoToolbarWrapper(navController, getString(context, R.string.modeChoosePageTitle)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedCard(
                        onClick = {
                            handler.data[username] = UserAccount(hashMapOf(), password, "personal")
                            handler.updateDatabase()

                            GlobalDatastore.username.value = username
                            GlobalDatastore.updatePreferences()

                            navController.navigate("personalDashboardPage")
                        }
                    ) {
                        Box(
                            modifier = Modifier.size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getString(context, R.string.homePage1),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    ElevatedCard(
                        onClick = {
                            handler.data[username] = UserAccount(hashMapOf(), password, "student")
                            handler.updateDatabase()

                            GlobalDatastore.username.value = username
                            GlobalDatastore.updatePreferences()

                            navController.navigate("studentDashboardPage")
                        }
                    ) {
                        Box(
                            modifier = Modifier.size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getString(context, R.string.homePage2),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
                ElevatedCard(
                    onClick = {
                        handler.data[username] = UserAccount(hashMapOf(), password, "teacher")
                        handler.updateDatabase()

                        GlobalDatastore.username.value = username
                        GlobalDatastore.updatePreferences()

                        navController.navigate("teacherDashboardPage")
                    }
                ) {
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getString(context, R.string.homePage3),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}