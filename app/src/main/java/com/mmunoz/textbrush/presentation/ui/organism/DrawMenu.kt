package com.mmunoz.textbrush.presentation.ui.organism

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmunoz.textbrush.R
import com.mmunoz.textbrush.presentation.ui.theme.TextBrushTheme

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

@Preview(showBackground = true)
@Composable
internal fun DrawMenuPreview() {
    TextBrushTheme(darkTheme = true) {
        DrawMenu(
            onDoAction = {},
            onEditAction = {},
            onReDoAction = {},
            onResetAction = {},
            onFontRandomAction = {},
            redoVisibility = true,
            undoVisibility = true
        )
    }
}