package com.example.cs4131_project.pages.dashboardPages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.DashboardWrapper
import com.example.cs4131_project.components.utility.DoubleLazyColumn
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.SavedItem
import com.example.cs4131_project.model.firestoreModels.UserAccount
import com.example.cs4131_project.model.graph.GraphViewModel
import com.google.gson.Gson

@Composable
fun StudentDashboardPage(navController: NavController, handler: FirestoreHandler, graphViewModel: GraphViewModel) {
    val context = LocalContext.current
    val gson = Gson()

    var userAccount: UserAccount?

    DashboardWrapper(
        navController,
        getString(context, R.string.studentDashboardPageTitle) + GlobalDatastore.username.value + "!",
        "student"
    ) {
        if (handler.data[GlobalDatastore.username.value] != null) {
            userAccount = handler.data[GlobalDatastore.username.value]
            val json = gson.toJson(handler.data)
            Log.i("StudentDashboardPage", json)

            DoubleLazyColumn(
                items = ArrayList(userAccount?.savedData?.toList()!!).apply {
                    add(0, "new" to SavedItem())
                },
                onClick = { item ->
                    if (item.second.isEmpty()) {
                        navController.navigate("createNewPage/student")
                    } else if (item.second.izNotesItem) {
                        navController.navigate("notesPage/personal/${item.second.notesItem?.notesContent}/${item.first}")
                    } else {
                        graphViewModel.equations = item.second.graphItem?.equations!!
                        navController.navigate("graphPage/personal/${item.first}")
                    }
                }
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
