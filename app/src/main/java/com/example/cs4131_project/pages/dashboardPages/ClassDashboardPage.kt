package com.example.cs4131_project.pages.dashboardPages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.components.utility.DoubleLazyColumn
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.SavedItem
import com.example.cs4131_project.model.firestoreModels.UserAccount
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graph.GraphViewModel
import com.google.gson.Gson

@Composable
fun ClassDashboardPage(navController: NavController, handler: FirestoreHandler, graphViewModel: GraphViewModel, graph3ViewModel: Graph3ViewModel, mode: String, className: String) {
    val context = LocalContext.current

    var userAccount: UserAccount?
    var done by remember{mutableStateOf(false)}

    handler.updateData { done = true }

    if (done) {
        DashboardWrapper(
            navController,
            getString(
                context,
                R.string.classDashboardPageTitle
            ) + className,
            mode, handler = handler
        ) {
            if (handler.data[className] != null) {
                userAccount = handler.data[className]

                DoubleLazyColumn(
                    items = ArrayList(userAccount?.savedData?.toList()!!).apply {
                        if (mode == "teacher") {
                            add(0, "Create New" to SavedItem())
                        }
                    },
                    onClick = { item ->
                        if (item.second.isEmpty()) {
                            navController.navigate("createNewPage/$mode")
                        } else if (item.second.izNotesItem) {
                            navController.navigate("notesPage/$mode/${item.second.notesItem?.notesContent}/${item.first}")
                        } else {
                            if (item.second.iz3d) {
                                graph3ViewModel.equation.value = item.second.graph3Item?.equation!!
                                navController.navigate("graph3Page/$mode/${item.first}")
                            }
                            else {
                                graphViewModel.equations = item.second.graphItem?.equations!!
                                navController.navigate("graphPage/$mode/${item.first}")
                            }
                        }
                    },
                    modifier = Modifier.padding(5.dp)
                ) { item ->
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (item.second.isEmpty()) {
                            Icon(
                                painter = painterResource(R.drawable.plus),
                                contentDescription = "New",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else if (item.second.izNotesItem) {
                            Icon(
                                painter = painterResource(R.drawable.note),
                                contentDescription = "Notes",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.chart_bell_curve_cumulative),
                                contentDescription = "Graph",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        Text(
                            text = item.first,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}