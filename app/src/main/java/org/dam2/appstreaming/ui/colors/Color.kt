package org.dam2.appstreaming.ui.colors
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Colores app SeaStream
val SeaBlueDark = Color(0xFF003366)  // Azul profundo
val SeaBlueLight = Color(0xFF00A8E8) // Azul turquesa claro
val SeaGradient = Brush.horizontalGradient(
    colors = listOf(SeaBlueDark, SeaBlueLight)
)
