package com.example.cs4131_project.pages.dashboardPages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun PersonalDashboardPage(navController: NavController, handler: FirestoreHandler, graphViewModel: GraphViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userAccount: UserAccount?
    var done by remember{mutableStateOf(false)}

    handler.updateData { done = true }

    if (done) {
        LaunchedEffect(Unit) {
            scope.launch {
                delay(300)
                GlobalDatastore.confettiShown = true
            }
        }

        if (GlobalDatastore.confettiEnabled && !GlobalDatastore.confettiShown) {
            KonfettiView(
                modifier = Modifier.fillMaxSize().zIndex(1f),
                parties = listOf(
                    Party(
                        emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(1.0, 0.7),
                        angle = Angle.TOP - 30,
                        spread = 45
                    ),
                    Party(
                        emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.0, 0.7),
                        angle = Angle.TOP + 30,
                        spread = 45
                    )
                )
            )
        }

        DashboardWrapper(
            navController,
            getString(
                context,
                R.string.personalDashboardPageTitle
            ) + GlobalDatastore.username.value + "!",
            "personal", handler = handler
        ) {
            if (handler.data[GlobalDatastore.username.value] != null) {
                userAccount = handler.data[GlobalDatastore.username.value]

                DoubleLazyColumn(
                    items = ArrayList(userAccount?.savedData?.toList()!!).apply {
                        add(0, "Create New" to SavedItem())
                    },
                    onClick = { item ->
                        if (item.second.isEmpty()) {
                            navController.navigate("createNewPage/personal")
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
}