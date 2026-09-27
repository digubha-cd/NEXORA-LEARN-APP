package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraSurface

/**
 * Reusable Card component adhering to the premium light aesthetic
 * with rounded corners, subtle border, optional gradient fill, and soft shadow.
 */
@Composable
fun NexoraCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = NexoraBorder,
    containerColor: Color = NexoraSurface,
    backgroundBrush: Brush? = null,
    cornerRadius: Dp = 18.dp,
    contentPadding: Dp = 16.dp,
    elevation: Dp = 1.5.dp,
    testTag: String = "nexora_card",
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardColors = CardDefaults.cardColors(
        containerColor = if (backgroundBrush != null) Color.Transparent else containerColor
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = shape,
            border = BorderStroke(1.dp, borderColor),
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            modifier = modifier
                .fillMaxWidth()
                .testTag(testTag)
        ) {
            if (backgroundBrush != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundBrush)
                ) {
                    Column(
                        modifier = Modifier.padding(contentPadding),
                        content = content
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(contentPadding),
                    content = content
                )
            }
        }
    } else {
        Card(
            shape = shape,
            border = BorderStroke(1.dp, borderColor),
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            modifier = modifier
                .fillMaxWidth()
                .testTag(testTag)
        ) {
            if (backgroundBrush != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundBrush)
                ) {
                    Column(
                        modifier = Modifier.padding(contentPadding),
                        content = content
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(contentPadding),
                    content = content
                )
            }
        }
    }
}

