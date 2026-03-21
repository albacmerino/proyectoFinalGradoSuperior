package org.dam2.appstreaming.ui.screen.pelicula

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * Fragmento que actúa como contenedor para la pantalla de detalle de película.
 */
class PeliculaDetailFragment : Fragment() {

    // Usamos activityViewModels para compartir el ViewModel con el HomeFragment
    private val viewModel: PeliculaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Observamos el estado unificado del ViewModel
                val state by viewModel.state.collectAsState()

                MaterialTheme {
                    // Invocamos la Screen (UI modularizada), pasando el estado y los eventos
                    PeliculaDetailScreen(
                        state = state,
                        onBackClick = { 
                            // Navegación hacia atrás
                            findNavController().popBackStack() 
                        },
                        onPlayTrailerClick = { key ->
                            // Abre el trailer en YouTube
                            val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$key".toUri())
                            startActivity(intent)
                        },
                        onWatchNowClick = { url ->
                            // Abre el enlace de la plataforma de streaming
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)
                        },
                        onSeeAllReviewsClick = { movieId ->
                            // Abre la web de TMDB para ver todas las reseñas
                            val url = "https://www.themoviedb.org/movie/$movieId/reviews?language=es-ES"
                            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                        },
                        onRecommendationClick = { movie ->
                            // Al pulsar una recomendación, actualizamos el ViewModel con la nueva película
                            viewModel.setSelectedItem(movie)
                        }
                    )
                }
            }
        }
    }
}
