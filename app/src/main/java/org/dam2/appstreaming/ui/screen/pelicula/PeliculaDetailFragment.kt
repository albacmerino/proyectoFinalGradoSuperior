package org.dam2.appstreaming.ui.screen.pelicula

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.SeaRatingBar
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.Genero

class PeliculaDetailFragment : Fragment() {

    private val viewModel: PeliculaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val selectedItem by viewModel.selectedPelicula.collectAsState()
                val allGenres by viewModel.allGenres.collectAsState()

                MaterialTheme {
                    selectedItem?.let { item ->
                        PeliculaDetailScreen(
                            item = item,
                            allGenres = allGenres,
                            onBackClick = { findNavController().popBackStack() }
                        )
                    } ?: Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF000814)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SeaBlueLight)
                    }
                }
            }
        }
    }
}

@Composable
fun PeliculaDetailScreen(
    item: FichaPelicula,
    allGenres: List<Genero>,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val nombreGenero = item.genreIds?.mapNotNull { id ->
        allGenres.find { it.id == id }?.name
    }?.joinToString(" • ") ?: "General"

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF000814))) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)) {
            Box(modifier = Modifier
                .height(350.dp)
                .fillMaxWidth()) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w1280${item.backdropPath}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF000814)),
                            startY = 400f
                        )
                    ))
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.displaySmall.copy(brush = SeaGradient, fontWeight = FontWeight.ExtraBold),
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // METADATOS UNIFICADOS (Igual que en Series)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.releaseDate?.take(4) ?: "N/A", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

                    Text(text = " • ", color = SeaBlueLight, modifier = Modifier.padding(horizontal = 6.dp))

                    Text(
                        text = nombreGenero,
                        color = SeaBlueLight.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        maxLines = 1, // Importante para que no rompa el diseño si el nombre es largo
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = " • ", color = SeaBlueLight, modifier = Modifier.padding(horizontal = 6.dp))

                    Surface(
                        color = SeaBlueLight.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SeaBlueLight.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "PELÍCULA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = SeaBlueLight,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SeaRatingBar(rating = (item.voteAverage / 2).toInt())
                    Text(text = "  ${String.format("%.1f", item.voteAverage)}", color = SeaBlueLight, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(2.dp, SeaGradient, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("AÑADIR A MI LISTA", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Sinopsis", style = MaterialTheme.typography.titleLarge, color = SeaBlueLight, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = item.overview.ifEmpty { "No hay descripción disponible." }, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f), lineHeight = 26.sp, modifier = Modifier.padding(bottom = 40.dp))
            }
        }
        IconButton(onClick = onBackClick, modifier = Modifier
            .padding(top = 48.dp, start = 16.dp)
            .size(45.dp)
            .background(Color.Black.copy(alpha = 0.6f), CircleShape)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
        }
    }
}