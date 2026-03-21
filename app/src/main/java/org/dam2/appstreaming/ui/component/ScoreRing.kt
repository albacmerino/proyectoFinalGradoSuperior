package org.dam2.appstreaming.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreRing(score: Double, modifier: Modifier = Modifier) {
    val progress = (score / 10).toFloat()

    // Lógica de colores SeaStream para la puntuación
    val color = when {
        score >= 7.0 -> Color(0xFF4CAF50) // Verde
        score >= 5.0 -> Color(0xFFFFEB3B) // Amarillo
        else -> Color(0xFFF44336)         // Rojo
    }

    Box(
        modifier = modifier
            .size(38.dp)
            .background(Color(0xFF000814), shape = CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 3.dp,
            trackColor = color.copy(alpha = 0.2f),
        )
        Text(
            text = "${(score * 10).toInt()}%",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

