package com.mmunoz.textbrush.presentation.ui.organism

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.mmunoz.textbrush.domain.controller.TextBrushDrawController
import com.mmunoz.textbrush.domain.controller.rememberTextBrushDrawController
import com.mmunoz.textbrush.presentation.extension.createPath
import com.mmunoz.textbrush.presentation.extension.drawTextOnCanvas
import com.mmunoz.textbrush.presentation.ui.theme.TextBrushTheme

@Composable
internal fun DrawContainer(
    modifier: Modifier = Modifier,
    drawText: String,
    controller: TextBrushDrawController,
    typeface: Typeface,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> },
) {
    val state by controller.finalPathList.collectAsState(initial = listOf())

    LaunchedEffect(controller) {
        controller.trackHistory(this, trackHistory)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        controller.insertNewPath(offset)
                    },
                    onDragEnd = {
                        controller.setFinalPath()
                    }
                ) { change, _ ->
                    val newPoint = change.position
                    controller.updateLatestPath(newPoint)
                }
            }
    ) {
        controller.pathList.forEach { pw ->
            drawPath(
                createPath(pw.points),
                color = Color.Green,
                alpha = 1f,
                style = Stroke(
                    width = 10f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        if (state.isNotEmpty()) {
            state.forEach { item ->
                drawTextOnCanvas(drawText, item.points, typeface)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DrawContainerPreview() {
    TextBrushTheme {
        DrawContainer(
            drawText = "Brush",
            controller = rememberTextBrushDrawController(),
            typeface = Typeface.SANS_SERIF,
            trackHistory = { _, _ -> }
        )
    }
}