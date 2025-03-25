package com.example.cs4131_project.pages.contentPages

import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.graphics.GraphRenderer2D
import com.example.cs4131_project.components.wrappers.ContentWrapper

@Composable
fun GraphPage(navController: NavController, mode: String) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    lateinit var renderer: GraphRenderer2D

    ContentWrapper(navController, getString(context, R.string.graphPageTitle), mode = mode,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("equationPage/$mode")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.function_variant),
                    contentDescription = "Equations"
                )
            }
        },
        menuItems = { expandedState ->
            DropdownMenuItem(
                text = {Text(context.getString(R.string.graphMenuItem1))},
                onClick = {
                    expandedState.value = false

                    renderer.recenter()
                }
            )
        }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                renderer = GraphRenderer2D(context, Paint().apply{
                    color = backgroundColor.toArgb()
                }).apply {}

                renderer
            },
            update = { view ->

            }
        )
    }
}