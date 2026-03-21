package org.dam2.appstreaming.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.dam2.appstreaming.ui.colors.SeaBlueLight

@Composable
fun MovieCard(movie: FichaPelicula, allGenres: List<Genero>, onClick: () -> Unit) {
    // 1. Protección de URL: Si rutaPoster es null, usamos un string vacío para que Coil no falle
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.rutaPoster ?: ""}"

    // 2. Búsqueda del género: Si la lista está vacía o no encuentra el ID, pone "Cine"
    val nombreGenero = allGenres.find { it.id == movie.idsGeneros?.firstOrNull() }?.name ?: "Cine"

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.height(180.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = movie.titulo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF001D3D)),
                contentScale = ContentScale.Crop
            )

            // Asegúrate de que ScoreRing sea accesible aquí
            ScoreRing(
                score = movie.puntuacionMedia,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .offset(x = 6.dp, y = 6.dp)
            )
        }

        Text(
            text = movie.titulo,
            color = Color.White,
            maxLines = 1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
        )

        Text(
            text = nombreGenero,
            color = SeaBlueLight.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
