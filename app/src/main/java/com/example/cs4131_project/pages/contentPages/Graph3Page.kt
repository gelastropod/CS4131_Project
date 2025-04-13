package com.example.cs4131_project.pages.contentPages

import android.graphics.Paint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs4131_project.MainActivity
import com.example.cs4131_project.R
import com.example.cs4131_project.components.graphics.openGL.GraphGLSurfaceView
import com.example.cs4131_project.components.utility.ExpandableFAB
import com.example.cs4131_project.components.utility.MiniFabItems
import com.example.cs4131_project.components.utility.noRippleClickable
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.Graph3ViewModel

@Composable
fun Graph3Page(navController: NavController, mode: String, graphViewModel: Graph3ViewModel = viewModel(), handler: FirestoreHandler, name: String) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val expandedState = remember{mutableStateOf(false)}
    var zoomScene by remember{mutableStateOf(true)}
    var centering by remember { mutableStateOf(true) }

    ContentWrapper(navController, getString(context, R.string.graphPageTitle), mode = mode,
        floatingActionButton = {
            ExpandableFAB(
                ArrayList(buildList {
                    addAll(it)
                    add(MiniFabItems(
                        icon = painterResource(R.drawable.function_variant),
                        title = "Equation",
                        onClick = {
                            navController.navigate("equationEditor3Page/$mode/$name")
                        }
                    ))
                    add(
                        MiniFabItems(
                            icon = painterResource(R.drawable.magnify_plus),
                            title = "Change zoom mode",
                            onClick = {
                                zoomScene = !zoomScene
                            }
                        )
                    )
                }),
                expandedState
            ) {expandedState.value = it}
        },
        originalName = name,
        handler = handler,
        topBarContent = {
            IconButton(
                onClick = {
                    centering = true
                }
            ) {
                Icon(painter = painterResource(R.drawable.home), contentDescription = "")
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                expandedState.value = false
            }) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    GraphGLSurfaceView(context, Paint().apply {
                        color = backgroundColor.toArgb()
                    }, graphViewModel, MainActivity.darkTheme, zoomScene, centering)
                },
                update = { view ->
                    view.zoomScene = zoomScene
                    if (centering) {
                        view.centering = centering
                        centering = false
                    }
                }
            )
        }
    }
}