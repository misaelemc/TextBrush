package com.mmunoz.textbrush.presentation.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.mmunoz.textbrush.R
import com.mmunoz.textbrush.presentation.ui.theme.TextBrushTheme

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
internal fun BrushTextDialogPreview() {
    TextBrushTheme {
        BrushTextDialog(
            textValue = "",
            onDoneAction = {},
            onValueChange = {},
        )
    }
}