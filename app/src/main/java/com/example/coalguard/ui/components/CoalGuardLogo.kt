package com.example.coalguard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CoalGuardLogo(
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    goldRingColor: Color = Color(0xFFD97706),
    navyBgColor: Color = Color(0xFF002B49)
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(navyBgColor)
            .border(1.5.dp, goldRingColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.7f)) {
            val width = this.size.width
            val height = this.size.height

            // 1. Draw Central Mining Shield Outline
            val shieldPath = Path().apply {
                moveTo(width * 0.2f, height * 0.15f)
                lineTo(width * 0.8f, height * 0.15f)
                lineTo(width * 0.8f, height * 0.55f)
                cubicTo(
                    width * 0.8f, height * 0.8f,
                    width * 0.5f, height * 0.95f,
                    width * 0.5f, height * 0.98f
                )
                cubicTo(
                    width * 0.5f, height * 0.95f,
                    width * 0.2f, height * 0.8f,
                    width * 0.2f, height * 0.55f
                )
                close()
            }

            drawPath(
                path = shieldPath,
                color = goldRingColor,
                style = Stroke(width = 2.dp.toPx())
            )

            // 2. Draw Crossed Pickaxe Line 1 (Top-Left to Bottom-Right)
            drawLine(
                color = Color.White,
                start = Offset(width * 0.3f, height * 0.3f),
                end = Offset(width * 0.7f, height * 0.7f),
                strokeWidth = 2.5.dp.toPx()
            )

            // 3. Draw Crossed Pickaxe Line 2 (Top-Right to Bottom-Left)
            drawLine(
                color = Color.White,
                start = Offset(width * 0.7f, height * 0.3f),
                end = Offset(width * 0.3f, height * 0.7f),
                strokeWidth = 2.5.dp.toPx()
            )

            // 4. Draw Miner Safety Helmet / Gold Star Emblem Center
            drawCircle(
                color = goldRingColor,
                radius = width * 0.12f,
                center = Offset(width * 0.5f, height * 0.45f)
            )

            drawCircle(
                color = Color.White,
                radius = width * 0.06f,
                center = Offset(width * 0.5f, height * 0.45f)
            )
        }
    }
}
