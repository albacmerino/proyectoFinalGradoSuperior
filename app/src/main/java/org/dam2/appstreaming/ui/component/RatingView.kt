package org.dam2.appstreaming.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dam2.appstreaming.ui.colors.SeaBlueLight

/**
 * COMPONENTE: BARRA DE CALIFICACIÓN
 * 
 * Implementa una visualización de puntuación basada en estrellas.
 *
 */
@Composable
fun SeaRatingBar(
    rating: Int,          // Valor actual (de 0 a maxRating)
    maxRating: Int = 5,   // Por defecto 5 estrellas
    onRatingChange: (Int) -> Unit = {} // Acción al pulsar (vacío por defecto para solo lectura)
) {
    Row {
        for (i in 1..maxRating) {
            val isSelected = i <= rating

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Estrella $i",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { 
                        // Invoca el callback solo si se ha proporcionado una acción
                        onRatingChange(i) 
                    }
                    .padding(horizontal = 2.dp),
                tint = if (isSelected) SeaBlueLight else Color.DarkGray.copy(alpha = 0.5f)
            )
        }
    }
}
