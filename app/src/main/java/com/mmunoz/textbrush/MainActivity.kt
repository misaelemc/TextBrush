package com.mmunoz.textbrush

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmunoz.textbrush.draw.TextBrushDrawController
import com.mmunoz.textbrush.draw.extension.createPath
import com.mmunoz.textbrush.draw.extension.drawTextOnCanvas
import com.mmunoz.textbrush.draw.rememberTextBrushDrawController
import com.mmunoz.textbrush.ui.theme.TextBrushTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextBrushTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextBrushPage()
                }
            }
        }
    }
}

@Composable
internal fun TextBrushPage(modifier: Modifier = Modifier) {
    var typeface by remember { mutableStateOf(Typeface.DEFAULT) }
    var drawnText by remember { mutableStateOf("Brush") }
    val openDialog = remember { mutableStateOf(false) }
    val undoVisibility = remember { mutableStateOf(false) }
    val redoVisibility = remember { mutableStateOf(false) }

    val controller = rememberTextBrushDrawController()
    val typefaceList: List<Typeface> = listOf(
        Typeface.DEFAULT,
        Typeface.DEFAULT_BOLD,
        Typeface.SANS_SERIF,
    )

    Box(modifier = modifier.background(MaterialTheme.colorScheme.onSurface)) {
        Image(
            painter = painterResource(id = R.drawable.bg_hallowen),
            contentDescription = "background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Column(
            modifier = Modifier

        ) {
            DrawContainer(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
                drawText = drawnText,
                typeface = typeface
            ) { undoCount, redoCount ->
                undoVisibility.value = undoCount != 0
                redoVisibility.value = redoCount != 0
            }
        }

        DrawMenu(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onDoAction = {
                controller.unDo()
            },
            onReDoAction = {
                controller.reDo()
            },
            onEditAction = {
                openDialog.value = true
            },
            undoVisibility = undoVisibility.value,
            redoVisibility = redoVisibility.value,
            onResetAction = { controller.reset() },
            onFontRandomAction = {
                typeface = typefaceList.random()
            }
        )
    }

    if (openDialog.value) {
        BrushTextDialog(
            textValue = drawnText,
            onValueChange = {
                drawnText = it
            },
            onDoneAction = {
                openDialog.value = false
            }
        )
    }
}

@Composable
internal fun DrawMenu(
    modifier: Modifier = Modifier,
    onDoAction: () -> Unit,
    onReDoAction: () -> Unit,
    onEditAction: () -> Unit,
    onResetAction: () -> Unit,
    onFontRandomAction: () -> Unit,
    redoVisibility: Boolean,
    undoVisibility: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onEditAction) {
            Icon(
                painter = painterResource(id = R.drawable.ic_outlined_edit),
                contentDescription = "edit_button",
                tint = MaterialTheme.colorScheme.background
            )
        }

        IconButton(onClick = onFontRandomAction) {
            Icon(
                painter = painterResource(id = R.drawable.ic_outlined_font_random),
                contentDescription = "font_button",
                tint = MaterialTheme.colorScheme.background
            )
        }

        AnimatedVisibility(undoVisibility) {
            IconButton(onClick = onDoAction) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outlined_undo),
                    contentDescription = "undo_button",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        AnimatedVisibility(redoVisibility) {
            IconButton(onClick = onReDoAction) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outlined_redo),
                    contentDescription = "redo_button",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        AnimatedVisibility(redoVisibility || undoVisibility) {
            IconButton(onClick = onResetAction) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outlined_reset_alt),
                    contentDescription = "reset_button",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

@Composable
internal fun DrawContainer(
    modifier: Modifier = Modifier.fillMaxSize(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BrushTextDialog(
    textValue: String,
    onValueChange: (String) -> Unit,
    onDoneAction: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.text_brush_input_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = textValue,
                    onValueChange = onValueChange,
                    label = {
                        Text(text = stringResource(id = R.string.text_brush_title))
                    },
                    modifier = Modifier,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onDoneAction()
                        },
                    ),
                    singleLine = true
                )
            }
        },
        onDismissRequest = onDoneAction,
        confirmButton = {
            Button(
                modifier = Modifier,
                onClick = onDoneAction
            ) { Text(text = stringResource(id = R.string.save_text)) }
        },
        dismissButton = {
            Button(
                modifier = Modifier,
                onClick = onDoneAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) { Text(text = stringResource(id = R.string.dismiss_text)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
internal fun TextBrushPreview() {
    TextBrushTheme {
        TextBrushPage()
    }
}