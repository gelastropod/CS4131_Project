package com.example.cs4131_project.model.graph

import android.graphics.Paint
import com.example.cs4131_project.model.graphics.GridDrawer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D

data class Equation(val equation: (Double) -> Double, val color: Point, val precision: Int = 1000) {
    fun drawOnGrid(gridDrawer: GridDrawer, viewPoint: Point2D, size: Point2D, backgroundColor: Point) {
        gridDrawer.drawGraph(viewPoint - size, viewPoint + size, color, backgroundColor, precision, equation)
    }
}