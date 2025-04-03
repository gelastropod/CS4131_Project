package com.example.cs4131_project.pages.contentPages

import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.DropdownMenuItem
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.Equation
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.utility.Point

object EquationPage {
    lateinit var selected: MutableState<Int>
    var initialised = false
}

@Composable
fun MathInputApp(graphViewModel: GraphViewModel, index: Int) {
    val latexContentState = remember{ mutableStateOf("")}

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
                <p>\(f(x)=${graphViewModel.equations[index].equationString}\)</p>
            </body>
            </html>
        """
    }

    updateLaTeX()

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

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        EquationPage.selected.value = index
                    }
                    false
                }

                setBackgroundColor(Color.TRANSPARENT)
            }
        },
        update = { webView ->
            webView.loadData(webViewState.value, "text/html", "UTF-8")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
fun EquationPage(navController: NavController, mode: String, graphViewModel: GraphViewModel = viewModel(), handler: FirestoreHandler, name: String) {
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
        handler = handler,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("graphPage/$mode/$name")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            handler.unsaved = true

                            graphViewModel.addEquation("", Point(1.0, 0.5, 0.5))

                            handler.unsavedData[GlobalDatastore.username.value]?.savedData?.get(name)?.graphItem?.equations = graphViewModel.equations
                        }
                    ) {
                        Text(getString(context, R.string.equationPage2))
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            handler.unsaved = true

                            navController.navigate("equationEditorPage/$mode/${EquationPage.selected.value}/$name")
                        },
                        enabled = EquationPage.selected.value != -1
                    ) {
                        Text(getString(context, R.string.contentWrapper9))
                    }
                    Button(
                        onClick = {
                            handler.unsaved = true

                            graphViewModel.removeEquation(EquationPage.selected.value)
                            EquationPage.selected.value = -1

                            handler.unsavedData[GlobalDatastore.username.value]?.savedData?.get(name)?.graphItem?.equations = graphViewModel.equations
                        },
                        enabled = EquationPage.selected.value != -1
                    ) {
                        Text(getString(context, R.string.contentWrapper10))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            items(graphViewModel.equations.size) { index ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {},
                    shape = RectangleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor =
                    if (EquationPage.selected.value == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                ) {
                    MathInputApp(graphViewModel, index)
                }
            }
        }
    }
}