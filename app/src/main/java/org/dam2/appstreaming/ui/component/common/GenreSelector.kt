package org.dam2.appstreaming.ui.component.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.ui.colors.SeaBlueLight

/**
 * COMPONENTE: SELECTOR DE GÉNEROS
 * 
 * Permite al usuario filtrar el contenido multimedia por categorías cinematográficas.
 * Utiliza un carrusel horizontal de chips interactivos (FilterChips) siguiendo las directrices de Material 3.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelector(
    generos: List<Genero>,
    selectedId: Int?,
    onClick: (Int?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onClick(null) },
                label = { Text("Todo") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SeaBlueLight,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                )
            )
        }
        
        // Generación dinámica de chips basados en la lista de géneros de la API
        items(generos) { genero ->
            FilterChip(
                selected = selectedId == genero.id,
                onClick = { onClick(genero.id) },
                label = { Text(genero.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SeaBlueLight,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                )
            )
        }
    }
}
