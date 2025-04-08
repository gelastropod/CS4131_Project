package com.example.cs4131_project.model.graph

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter

class Graph3ViewModel : ViewModel() {
    var viewPoint by mutableStateOf(Point(0.0, 0.0, 0.0))
    var size by mutableStateOf(Point(10.5, 10.5, 10.5))
    var equation = mutableStateOf(Equation3("f(x,y)=", Point(1.0, 1.0, 1.0)))
    var power10 by mutableStateOf(0)
    var minorSpace by mutableStateOf(Point(1.0, 1.0, 1.0))
    var majorSpace by mutableStateOf(Point(5.0, 5.0, 5.0))
    var screenSpace by mutableStateOf(Point2D(0.0, 0.0))
}