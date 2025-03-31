package com.example.cs4131_project.model.graph

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D

class GraphViewModel : ViewModel() {
    var viewPoint by mutableStateOf(Point2D(0.0, 0.0))
    var size by mutableStateOf(Point2D(10.5, 10.5))
    var equations by mutableStateOf(ArrayList<Equation>())
    var power10 by mutableStateOf(0)
    var minorSpace by mutableStateOf(Point2D(1.0, 1.0))
    var majorSpace by mutableStateOf(Point2D(5.0, 5.0))
    var screenSpace by mutableStateOf(Point2D(0.0, 0.0))

    fun addEquation(equation: Equation) {
        equations = ArrayList(equations).apply {
            add(equation)
        }
    }

    fun removeEquation(index: Int) {
        if (index in equations.indices) {
            equations = ArrayList(equations).apply { removeAt(index) }
        }
    }

    fun clearEquations() {
        equations = ArrayList()
    }
}