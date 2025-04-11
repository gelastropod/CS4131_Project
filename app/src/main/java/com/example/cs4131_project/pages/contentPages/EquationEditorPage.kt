package com.example.cs4131_project.pages.contentPages

import android.graphics.Color
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.utility.Point
import katex.hourglass.`in`.mathlib.MathView

@Composable
fun EquationEditorPage(navController: NavController, mode: String, index: Int, graphViewModel: GraphViewModel, handler: FirestoreHandler, name: String) {
    val inputExpressionState = remember { mutableStateOf(graphViewModel.equations[index].equationString) }
    val context = LocalContext.current

    ContentWrapper(navController, getString(context, R.string.equationEditorPageTitle), mode = mode, handler = handler, originalName = name, backRoute = "equationPage/$mode/$name") {
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
                        setDisplayText("\$f(x)=${inputExpressionState.value}\$")
                        mathViewRef.value = this
                    }
                },
                update = {
                    it.setDisplayText("\$f(x)=${inputExpressionState.value}\$")
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        handler.unsaved = true

                        graphViewModel.setEquation(index, inputExpressionState.value, Point(1.0, 0.5, 0.5))

                        val key = if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                        handler.unsavedData[key]?.savedData?.get(name)?.graphItem?.equations = graphViewModel.equations

                        navController.navigate("equationPage/$mode/$name")
                    }
                ) {
                    Text("Confirm")
                }
                Button(
                    onClick = {
                        navController.navigate("equationPage/$mode/$name")
                    }
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}