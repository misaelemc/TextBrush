package com.mmunoz.textbrush.presentation.ui.page

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmunoz.textbrush.R
import com.mmunoz.textbrush.domain.controller.rememberTextBrushDrawController
import com.mmunoz.textbrush.presentation.ui.organism.DrawContainer
import com.mmunoz.textbrush.presentation.ui.organism.DrawMenu
import com.mmunoz.textbrush.presentation.ui.theme.TextBrushTheme

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

@Preview(showBackground = true)
@Composable
internal fun TextBrushPagePreview() {
    TextBrushTheme {
        TextBrushPage()
    }
}