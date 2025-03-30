package com.example.cs4131_project.pages.contentPages

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper

object EquationPage {
    lateinit var selected: MutableState<Int>
    var initialised = false
}

@Composable
fun MathInputApp() {
    val inputExpressionState = remember{ mutableStateOf("")}
    val latexContentState = remember{ mutableStateOf("")}
    val context = LocalContext.current

    // Function to update LaTeX content
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
                <p>\(${inputExpressionState.value}\)</p>
            </body>
            </html>
        """
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            value = inputExpressionState.value,
            onValueChange = { inputExpressionState.value = it },
            placeholder = { Text(getString(context, R.string.equationPage1)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val webViewState = rememberUpdatedState(latexContentState.value)

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
                webView.loadData(webViewState.value, "text/html", "UTF-8")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { updateLaTeX() }) {
            Text("Render LaTeX")
        }
    }
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
    ) {/*
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
        }*/
        MathInputApp()
    }
}