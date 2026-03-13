package org.dam2.appstreaming.ui.screen.pelicula

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import org.dam2.appstreaming.ui.colors.*
import org.dam2.appstreaming.ui.component.SeaRatingBar

class PeliculaDetailFragment : Fragment() {
    private val viewModel: PeliculaViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val selectedItem by viewModel.selectedPelicula.collectAsState()
                MaterialTheme {
                    selectedItem?.let { pelicula ->
                        PeliculaDetailScreen(pelicula) { findNavController().popBackStack() }
                    }
                }
            }
        }
    }
}

@Composable
fun PeliculaDetailScreen(item: org.dam2.appstreaming.ui.component.FichaPelicula, onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000814))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            // Imagen Backdrop
            Box(modifier = Modifier.height(350.dp).fillMaxWidth()) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w1280${item.backdropPath}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF000814)), startY = 400f)))
            }
            // Detalles
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = item.title, style = MaterialTheme.typography.displaySmall.copy(brush = SeaGradient, fontWeight = FontWeight.ExtraBold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Película • ${item.releaseDate?.take(4) ?: ""}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                SeaRatingBar(rating = (item.voteAverage / 2).toInt())
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp).border(2.dp, SeaGradient, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("AÑADIR A MI LISTA", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Sinopsis", color = SeaBlueLight, fontWeight = FontWeight.Bold)
                Text(text = item.overview, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 8.dp, bottom = 40.dp))
            }
        }
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 48.dp, start = 16.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
        }
    }
}