package com.example.cs4131_project.model.graph

import android.graphics.Paint
import android.util.Log
import com.example.cs4131_project.model.graphics.GridDrawer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import net.objecthunter.exp4j.ExpressionBuilder

data class Equation(val equation: (Double) -> Double?, val color: Point, val precision: Int = 500) {
    fun drawOnGrid(gridDrawer: GridDrawer, viewPoint: Point2D, size: Point2D, backgroundColor: Point) {
        gridDrawer.drawGraph(viewPoint - size, viewPoint + size, color, backgroundColor, precision, equation)
    }

    companion object {
        private fun latexToMath(latex: String): String {
            return latex
                .replace("f(x)=", "")
                .replace("\\frac{", "(")
                .replace("\\sqrt{", "sqrt(")
                .replace("\\sin{", "sin(")
                .replace("\\cos{", "cos(")
                .replace("\\tan{", "tan(")
                .replace("\\sec{", "sec(")
                .replace("\\csc{", "csc(")
                .replace("\\cot{", "cot(")
                .replace("\\sin^{-1}{", "asin(")
                .replace("\\cos^{-1}{", "acos(")
                .replace("\\tan^{-1}{", "atan(")
                .replace("\\log{", "log(")
                .replace("\\sqrt{", "sqrt(")
                .replace("\\|", "abs(")
                .replace("\\exp{", "exp(")
                .replace("ceil{", "ceil(")
                .replace("floor{", "floor(")
                .replace("\\sign{", "signum(")
                .replace("\\ln{", "ln(")
                .replace("\\cdot", "*")
                .replace("}^{", ")^(")
                .replace("}{", ")/(")
                .replace("}", ")")
                .replace("{", "(")
        }

        private fun parseLatexToFunction(latex: String): (Double) -> Double? {
            val mathExpression = latexToMath(latex)

            return { x: Double ->
                try {
                    val result = ExpressionBuilder(mathExpression)
                        .variable("x")
                        .build()
                        .setVariable("x", x)
                        .evaluate()

                    if (result.isNaN() || result.isInfinite()) {
                        null
                    } else {
                        result
                    }
                }
                catch (e: Exception) {
                    null
                }
            }
        }
    }

    constructor(latex: String, color: Point, precision: Int = 500) : this(parseLatexToFunction(latex), color, precision)
}