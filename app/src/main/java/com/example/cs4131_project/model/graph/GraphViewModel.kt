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

class GraphViewModel : ViewModel() {
    var viewPoint by mutableStateOf(Point2D(0.0, 0.0))
    var size by mutableStateOf(Point2D(10.5, 10.5))
    var equations by mutableStateOf(ArrayList<Equation>())
    var power10 by mutableStateOf(0)
    var minorSpace by mutableStateOf(Point2D(1.0, 1.0))
    var majorSpace by mutableStateOf(Point2D(5.0, 5.0))
    var screenSpace by mutableStateOf(Point2D(0.0, 0.0))

    fun addEquation(equation: String, color: Point) {
        equations = ArrayList(equations).apply {
            add(Equation(equation, color))
        }
    }

    fun removeEquation(index: Int) {
        if (index in equations.indices) {
            equations = ArrayList(equations).apply { removeAt(index) }
        }
    }

    fun setEquation(index: Int, equation: String, color: Point) {
        if (index in equations.indices) {
            equations = ArrayList(equations).apply {
                this[index] = Equation(equation, color)
            }
        }
    }

    fun clearEquations() {
        equations = ArrayList()
    }
}