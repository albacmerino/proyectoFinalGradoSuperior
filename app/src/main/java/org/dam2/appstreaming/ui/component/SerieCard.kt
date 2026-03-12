package org.dam2.appstreaming.ui.component

import androidx.compose.foundation.background
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
import org.dam2.appstreaming.data.TmdbConfig

@Composable
fun SerieCard(serie: FichaSerie) {
    val imageUrl = "https://image.tmdb.org/t/p/w500${serie.posterPath}"

    Column(modifier = Modifier.width(130.dp)) {
        Box(modifier = Modifier.height(180.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = serie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF001D3D)),
                contentScale = ContentScale.Crop
            )

            ScoreRing(
                score = serie.voteAverage,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .offset(x = 6.dp, y = 6.dp)
            )
        }
        Text(
            text = serie.title,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
        )
    }
}