package com.example.cs4131_project.pages.contentPages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.ContentWrapper

object EquationPage {
    lateinit var selected: MutableState<Int>
    var initialised = false
}

@Composable
fun EquationPage(navController: NavController, mode: String) {
    val context = LocalContext.current

    EquationPage.initialised = true
    EquationPage.selected = remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        EquationPage.selected.value = -1
    }

    ContentWrapper(navController,
        getString(context, R.string.equationPageTitle),
        EquationPage.selected.value,
        mode = mode,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("graphPage/$mode")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.chart_bell_curve_cumulative),
                    contentDescription = "Graph"
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(50) { index ->
                Button(
                    onClick = {
                        EquationPage.selected.value = index
                    },
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (EquationPage.selected.value == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Equation $index",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}