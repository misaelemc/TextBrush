package com.mmunoz.textbrush.presentation.extension

import android.graphics.Matrix
import android.graphics.PathMeasure
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/**
 * Creates a path based on a list of `Offset` points, defining a custom path.
 *
 * @param points A list of `Offset` points used to define the custom path.
 * @return A `Path` object representing the custom path created from the list of points.
 */
fun createPath(points: List<Offset>): Path {
    val path = Path()

    // Ensure there are at least two points to create a path
    if (points.size > 1) {
        var oldPoint: Offset? = null
        path.moveTo(points[0].x, points[0].y)

        for (i in 1 until points.size) {
            val point: Offset = points[i]
            oldPoint?.let {
                val midPoint = Offset((it.x + point.x) / 2, (it.y + point.y) / 2)
                if (i == 1) {
                    path.lineTo(midPoint.x, midPoint.y)
                } else {
                    path.quadraticBezierTo(it.x, it.y, midPoint.x, midPoint.y)
                }
            }
            oldPoint = point
        }
        oldPoint?.let { path.lineTo(it.x, oldPoint.y) }
    }

    return path
}

/**
 * Draws the specified text along a custom path on the canvas using the provided list of points.
 *
 * @param text The text to be rendered along the path.
 * @param points A list of `Offset` points that define the custom path.
 * @param defaultTypeface The typeface defined for the text.
 */
fun DrawScope.drawTextOnCanvas(
    text: String,
    points: List<Offset>,
    defaultTypeface: Typeface = Typeface.DEFAULT
) {
    // Create a path from the list of offsets
    val pathMeasure = PathMeasure(android.graphics.Path().apply {
        points.forEachIndexed { index, offset ->
            if (index == 0) {
                moveTo(offset.x, offset.y)
            } else {
                lineTo(offset.x, offset.y)
            }
        }
    }, false)

    // Calculate the length of the path and spacing for characters
    val pathLength = pathMeasure.length
    val charSpacing = pathLength / text.length

    // Configure the paint for drawing text
    val paint = Paint().asFrameworkPaint()
        .apply {
            // Set the text size and the text color
            textSize = 44.dp.toPx()
            color = Color.Red.toArgb()
            isAntiAlias = true
            typeface = defaultTypeface
        }

    // Draw each character along the custom path
    text.forEachIndexed { index, char ->
        val charPos = index * charSpacing
        val charOnPath = FloatArray(2)

        // Get the position of the character along the path
        pathMeasure.getPosTan(charPos, charOnPath, null)

        // Create a matrix for transformations if needed
        val matrix = Matrix()
        pathMeasure.getMatrix(charPos, matrix, PathMeasure.POSITION_MATRIX_FLAG)

        // Draw the character on the canvas
        drawContext.canvas.nativeCanvas.drawText(
            char.toString(),
            charOnPath[0],
            charOnPath[1],
            paint
        )
    }
}