package com.example.finances.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.finances.ui.theme.Purple1
import com.example.finances.ui.theme.Purple2

@Composable
fun RowScope.AppButton(
    @StringRes
    text: Int,
    onClick: () -> Unit,
    isActive: Boolean = false,
    textColor: Color = Color.White
) {
    val color = if (isActive) Purple2 else Purple1
    Button(
        modifier = Modifier
            .weight(1f)
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = onClick
    ) {
        Text(
            text = stringResource(text),
            textAlign = TextAlign.Left,
            color = textColor
        )
    }
}