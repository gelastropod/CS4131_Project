package com.example.cs4131_project.pages

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
import com.example.cs4131_project.components.DashboardWrapper.Companion.DashboardWrapper
import com.example.cs4131_project.components.DoubleLazyColumn.Companion.DoubleLazyColumn

class ClassDashboardPage {
    companion object {
        @Composable
        fun ClassDashboardPage(navController: NavController, mode: String) {
            val context = LocalContext.current

            DashboardWrapper(navController, getString(context, R.string.classDashboardPageTitle), mode) {
                DoubleLazyColumn(
                    items = arrayListOf(
                        Pair("notes", "test1"),
                        Pair("graph", "test2")
                    ),
                    onClick = {

                    }
                ) { item ->
                    Column (
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (item.first) {
                            "new" -> {
                                Icon(
                                    painter = painterResource(R.drawable.plus),
                                    contentDescription = "New",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                            "notes" -> {
                                Icon(
                                    painter = painterResource(R.drawable.note),
                                    contentDescription = "Notes",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                            "graph" -> {
                                Icon(
                                    painter = painterResource(R.drawable.chart_bell_curve_cumulative),
                                    contentDescription = "Graph",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                        Text(
                            text = item.second,
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