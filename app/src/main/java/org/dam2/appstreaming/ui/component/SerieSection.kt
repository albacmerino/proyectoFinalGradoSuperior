import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.ScoreRing

@Composable
fun SerieSection(title: String, series: List<FichaSerie>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(series) { serie ->
                // Creamos una carátula que use los datos de FichaSerie
                SerieCard(serie)
            }
        }
    }
}

@Composable
fun SerieCard(serie: FichaSerie) {
    // Reutilizamos el diseño de MovieCard pero con los datos de serie
    val imageUrl = "https://image.tmdb.org/t/p/w500${serie.posterPath}"

    Column(modifier = Modifier.width(130.dp)) {
        Box(modifier = Modifier.height(180.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = serie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Color(0xFF001D3D)
                    ),
                contentScale = ContentScale.Crop
            )
            ScoreRing(
                score = serie.voteAverage,
                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).offset(x = 6.dp, y = 6.dp)
            )
        }
        Text(
            text = serie.title, // En FichaSerie, 'title' mapea a 'name' de TMDB
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 10.dp, start = 4.dp)
        )
    }
}