package com.example.cs4131_project.pages.contentPages

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun EquationEditorPage(navController: NavController, mode: String, index: Int, graphViewModel: GraphViewModel, handler: FirestoreHandler, name: String) {
    val inputExpressionState = remember { mutableStateOf(graphViewModel.equations[index].equationString) }
    val latexContentState = remember { mutableStateOf("") }
    val context = LocalContext.current

    fun updateLaTeX() {
        latexContentState.value = """
            <!DOCTYPE html>
            <html>
            <head>
               <script type="text/javascript" async
                src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML">
                </script>
            </head>
            <body>
                <p>\(f(x)=${inputExpressionState.value}\)</p>
            </body>
            </html>
        """
    }

    ContentWrapper(navController, getString(context, R.string.equationEditorPageTitle), mode = mode, handler = handler) {
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

            updateLaTeX()

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                consoleMessage?.let {
                                    Toast.makeText(context, "Console message: ${it.message()}", Toast.LENGTH_SHORT).show()
                                    Log.e("WebView Console Error", it.message())
                                    return true
                                }
                                return false
                            }
                        }

                        loadData(latexContentState.value, "text/html", "UTF-8")
                    }
                },
                update = { webView ->
                    webView.loadData(latexContentState.value, "text/html", "UTF-8")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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

                        handler.unsavedData[GlobalDatastore.username.value]?.savedData?.get(name)?.graphItem?.equations = graphViewModel.equations

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