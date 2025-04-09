package com.example.cs4131_project.pages.contentPages

import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.utility.Point
import katex.hourglass.`in`.mathlib.MathView

@Composable
fun EquationEditor3Page(navController: NavController, mode: String, graphViewModel: Graph3ViewModel, handler: FirestoreHandler, name: String) {
    val inputExpressionState = remember { mutableStateOf(graphViewModel.equation.value.equationString) }
    val context = LocalContext.current

    ContentWrapper(navController, getString(context, R.string.equationEditorPageTitle), mode = mode, handler = handler, originalName = name,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("graph3Page/$mode/$name")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.chart_bell_curve_cumulative),
                    contentDescription = "a"
                )
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = inputExpressionState.value,
                onValueChange = { newValue ->
                    inputExpressionState.value = newValue

                    handler.unsaved = true

                    graphViewModel.equation.value.equationString = inputExpressionState.value

                    val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                    handler.unsavedData[key]?.savedData?.get(name)?.graph3Item?.equation?.equationString = inputExpressionState.value
                },
                label = { Text("Enter LaTeX Equation") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val textColor = MaterialTheme.colorScheme.onBackground

            val mathViewRef = remember { mutableStateOf<MathView?>(null) }

            AndroidView(
                factory = { context ->
                    MathView(context).apply {
                        setBackgroundColor(Color.TRANSPARENT)
                        setTextColor(textColor.toArgb())
                        setDisplayText("\$f(x,y)=${inputExpressionState.value}\$")
                        mathViewRef.value = this
                    }
                },
                update = {
                    it.setDisplayText("\$f(x,y)=${inputExpressionState.value}\$")
                }
            )
        }
    }
}