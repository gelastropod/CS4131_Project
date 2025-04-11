package com.example.cs4131_project.pages.contentPages

import android.graphics.Color as Color2
import android.util.Log
import android.view.MotionEvent
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.utility.ExpandableFAB
import com.example.cs4131_project.components.utility.MiniFabItems
import com.example.cs4131_project.components.utility.noRippleClickable
import com.example.cs4131_project.components.wrappers.ContentWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.Equation
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.utility.Point
import katex.hourglass.`in`.mathlib.MathView
import kotlin.math.exp

object EquationPage {
    var initialised = false
}

@Composable
fun ColorPickerDialogDemo(graphViewModel: GraphViewModel, index: Int, handler: FirestoreHandler, mode: String) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(graphViewModel.equations[index].color) }

    Column(
        modifier = Modifier
            .size(50.dp, 50.dp)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(selectedColor.toColor(), shape = CircleShape)
                .clickable {
                    if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                        showDialog = true
                    }
                }
        )

        if (showDialog) {
            ColorPickerDialog(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    graphViewModel.equations[index].color = color
                    selectedColor = color

                    handler.unsaved = true

                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    selectedColor: Point,
    onColorSelected: (Point) -> Unit,
    onDismiss: () -> Unit
) {
    val colorOptions = arrayListOf(
        Point(1.0, 0.5, 0.5),
        Point(0.5, 0.5, 1.0)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a Color") },
        text = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                colorOptions.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color.toColor(), shape = CircleShape)
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EquationPage(
    navController: NavController,
    mode: String,
    graphViewModel: GraphViewModel = viewModel(),
    handler: FirestoreHandler,
    name: String
) {
    val context = LocalContext.current
    val expandedState = remember{mutableStateOf(false)}

    EquationPage.initialised = true

    ContentWrapper(navController,
        getString(context, R.string.equationPageTitle),
        mode = mode,
        handler = handler,
        originalName = name,
        floatingActionButton = {
            ExpandableFAB(
                ArrayList(buildList {
                    addAll(it)
                    add(MiniFabItems(
                        painterResource(R.drawable.chart_bell_curve_cumulative), "Graph",
                        onClick = {
                            navController.navigate("graphPage/$mode/$name")
                        }
                    ))
                }),
                expandedState
            ) {expandedState.value = it}
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                expandedState.value = false
            }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = graphViewModel.equations
                ) { equation ->
                    val index = graphViewModel.equations.indexOf(equation)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            onClick = {
                                if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                                    handler.unsaved = true

                                    navController.navigate("equationEditorPage/$mode/$index/$name")
                                }
                            },
                            shape = RectangleShape,
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            AndroidView(
                                factory = { context ->
                                    MathView(context).apply {
                                        setDisplayText("\$f(x)=${graphViewModel.equations[index].equationString}\$")
                                        setBackgroundColor(Color2.TRANSPARENT)
                                        setOnClickListener {
                                            if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                                                handler.unsaved = true

                                                navController.navigate("equationEditorPage/$mode/$index/$name")
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorPickerDialogDemo(graphViewModel, index, handler, mode)
                            if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
                                IconButton(
                                    onClick = {
                                        handler.unsaved = true

                                        graphViewModel.removeEquation(index)

                                        val key =
                                            if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                                        handler.unsavedData[key]?.savedData?.get(name)?.graphItem?.equations =
                                            graphViewModel.equations
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.plus),
                                        contentDescription = "Delete",
                                        modifier = Modifier.rotate(45f),
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                if (mode != "student" || GlobalDatastore.currentClass.value.isEmpty()) {
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

                                    val key =
                                        if (GlobalDatastore.currentClass.value.isEmpty()) GlobalDatastore.username.value else GlobalDatastore.currentClass.value
                                    handler.unsavedData[key]?.savedData?.get(name)?.graphItem?.equations =
                                        graphViewModel.equations
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(painter = painterResource(R.drawable.plus), contentDescription = "aa")
                                    Text(getString(context, R.string.equationPage2))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}