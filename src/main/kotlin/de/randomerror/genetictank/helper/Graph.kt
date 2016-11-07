package de.randomerror.genetictank.helper

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text

/**
 * Created by henri on 06.11.16.
 */

class Graph(val xAxisScale: Double, val xTicks: Int, val xLabel: String, val yAxisScale: Double, val yTicks: Int, val yLabel: String, val data: Map<Paint, Map<Double, Double>>) {

    val minYVal = data.flatMap { it.value.values }.min() ?: 0.0
    val maxYVal = data.flatMap { it.value.values }.max() ?: 0.0

    val minXVal = data.flatMap { it.value.keys }.min() ?: 0.0
    val maxXVal = data.flatMap { it.value.keys }.max() ?: 0.0

    val width = (maxXVal - minXVal) * xAxisScale
    val height = (maxYVal - minYVal) * yAxisScale

    fun render(gc: GraphicsContext) = gc.transformContext {
        renderGrid(gc)
        renderAxis(gc)

        transform(1.0, 0.0,
                0.0, -1.0,
                -minXVal * xAxisScale, maxYVal * yAxisScale)

        data.forEach { paint, d ->
            stroke = paint
            beginPath()
            d.forEach { k, v -> lineTo(k.toDouble() * xAxisScale, v * yAxisScale) }
            stroke()
        }
    }

    fun renderPoint(gc: GraphicsContext, x: Double, y: Double, radius: Double, fill: Paint) = gc.transformContext {
        transform(1.0, 0.0,
                0.0, -1.0,
                -minXVal * xAxisScale, maxYVal * yAxisScale)
        gc.fill = fill
        fillOval(x * xAxisScale - radius, y * yAxisScale - radius, radius * 2, radius * 2)
    }

    private fun renderGrid(gc: GraphicsContext) = gc.transformContext {
        lineWidth = 1.0
        stroke = Color.GREY
        (0..xTicks).forEach {
            strokeLine(it.toDouble() * (width / xTicks), 0.0, it.toDouble() * (width / xTicks), height)
            val text = Text("%.2f".format(minXVal + it.toDouble() * (width / xAxisScale / xTicks)))

            fillText("%.2f".format(minXVal + it.toDouble() * (width / xAxisScale / xTicks)), it.toDouble() * (width / xTicks) - text.boundsInLocal.width / 2, height + text.baselineOffset)
        }

        (0..yTicks).forEach {
            strokeLine(0.0, it.toDouble() * (height / yTicks), width, it.toDouble() * (height / yTicks))
            val text = Text("%.2f".format(maxYVal - it.toDouble() * (height / yAxisScale / yTicks)))

            fillText("%.2f".format(maxYVal - it.toDouble() * (height / yAxisScale / yTicks)), -text.boundsInLocal.width - 5.0, it.toDouble() * (height / yTicks) + text.baselineOffset / 2)
        }
    }

    private fun renderAxis(gc: GraphicsContext) = gc.transformContext {
        lineWidth = 1.0

        // draw xAxis
        strokeLine(0.0, maxYVal * yAxisScale, (maxXVal - minXVal) * xAxisScale, maxYVal * yAxisScale)

        // draw yAxis
        strokeLine(-minXVal * xAxisScale, 0.0, -minXVal * xAxisScale, (maxYVal - minYVal) * yAxisScale)
    }
}