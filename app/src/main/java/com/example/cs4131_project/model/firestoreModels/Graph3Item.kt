package com.example.cs4131_project.model.firestoreModels

import com.example.cs4131_project.model.graph.Equation
import com.example.cs4131_project.model.graph.Equation3
import com.example.cs4131_project.model.utility.Point

data class Graph3Item(var equation: Equation3, var quality: Int) {
    constructor(): this(Equation3("", Point(1.0, 1.0, 1.0)), 1)
}