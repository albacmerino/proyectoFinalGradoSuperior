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

@Composable
fun MovieCard(movie: FichaPelicula, onClick: () -> Unit) { // Añadimos onClick
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() } // Hacemos toda la tarjeta clickeable
    ) { // IMPORTANTE: Asegúrate de que posterPath no sea nulo
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

        Column(modifier = Modifier.width(130.dp)) {
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF001D3D)),
                    contentScale = ContentScale.Crop
                )

                // Llamamos a ScoreRing que ahora está en su propio archivo
                ScoreRing(
                    score = movie.voteAverage,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        // Importante: Asegúrate de tener importado androidx.compose.foundation.layout.offset
                        .offset(x = 6.dp, y = 6.dp)
                )
            }

            Text(
                text = movie.title,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp, start = 4.dp)
            )
        }
    }
}