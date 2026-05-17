package org.dam2.appstreaming.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.Genero
import org.dam2.appstreaming.ui.colors.SeaBlueLight

/**
 * COMPONENTE: FICHA DE PELÍCULA
 * 
 * Representa visualmente una película en las listas y carruseles.
 *
 */
@Composable
fun MovieCard(
    movie: FichaPelicula,
    allGenres: List<Genero>,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    // URL de la imagen basada en la configuración de TMDB (w500 para calidad media)
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.rutaPoster ?: ""}"

    // Mapeo de ID de género a nombre legible
    val nombreGenero = allGenres.find {
        it.id == movie.idsGeneros?.firstOrNull()
    }?.name ?: "Cine"

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.height(180.dp)) {
            // Imagen del póster con bordes redondeados
            AsyncImage(
                model = imageUrl,
                contentDescription = movie.titulo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF001D3D)),
                contentScale = ContentScale.Crop
            )

            // Botón favorito
            androidx.compose.material3.IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = if (isFavorite)
                        androidx.compose.material.icons.Icons.Default.Favorite
                    else
                        androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Anillo de puntuación
            ScoreRing(
                score = movie.puntuacionMedia,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .offset(x = 6.dp, y = 6.dp)
            )
        }

        // Título de la película
        Text(
            text = movie.titulo,
            color = Color.White,
            maxLines = 1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
        )

        // Género principal
        Text(
            text = nombreGenero,
            color = SeaBlueLight.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
