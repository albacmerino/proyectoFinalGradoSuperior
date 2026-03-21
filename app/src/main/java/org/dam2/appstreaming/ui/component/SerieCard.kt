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
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.data.model.Genero

@Composable
fun SerieCard(serie: FichaSerie, allGenres: List<Genero>, onClick: () -> Unit) {
    // Construcción de la URL y búsqueda del nombre del género
    val imageUrl = "https://image.tmdb.org/t/p/w500${serie.rutaPoster}"
    val nombreGenero = allGenres.find {
        it.id == serie.idsGeneros?.firstOrNull()
    }?.name ?: "Serie"

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        // Contenedor de Imagen + Puntuación
        Box(modifier = Modifier.height(180.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = serie.titulo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF001D3D)),
                contentScale = ContentScale.Crop
            )

            // Anillo de puntuación neón posicionado abajo a la derecha
            ScoreRing(
                score = serie.puntuacionMedia,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .offset(x = 6.dp, y = 6.dp)
            )
        }

        // Título de la serie (Fuera del Box para que aparezca debajo)
        Text(
            text = serie.titulo,
            color = Color.White,
            maxLines = 1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
        )

        // Nombre del Género con estética SeaStream
        Text(
            text = nombreGenero,
            color = SeaBlueLight.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
