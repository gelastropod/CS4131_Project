package com.example.cs4131_project.pages.contentPages

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs4131_project.MainActivity
import com.example.cs4131_project.R
import com.example.cs4131_project.components.graphics.openGL.GraphGLSurfaceView
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.graph.Graph3ViewModel

@Composable
fun Graph3Page(navController: NavController, mode: String, graphViewModel: Graph3ViewModel = viewModel(), handler: FirestoreHandler, name: String) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background

    ContentWrapper(navController, getString(context, R.string.graphPageTitle), mode = mode,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("equationEditor3Page/$mode/$name")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.function_variant),
                    contentDescription = "Equations"
                )
            }
        },
        originalName = name,
        handler = handler
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    GraphGLSurfaceView(context, Paint().apply {
                        color = backgroundColor.toArgb()
                    }, graphViewModel, MainActivity.darkTheme)
                },
                update = { view ->

                }
            )


        }
    }
}