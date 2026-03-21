package org.dam2.appstreaming.ui.screen.serie

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
 * Fragmento que actúa como contenedor para la pantalla de detalle de serie.
 */
class SerieDetailFragment : Fragment() {

    // Usamos activityViewModels para compartir el ViewModel con el HomeFragment
    private val viewModel: SerieViewModel by activityViewModels()

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
                    SerieDetailScreen(
                        state = state,
                        onBackClick = { 
                            findNavController().popBackStack() 
                        },
                        onPlayTrailerClick = { key ->
                            val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$key".toUri())
                            startActivity(intent)
                        },
                        onWatchNowClick = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)
                        },
                        onSeeAllReviewsClick = { serieId ->
                            val url = "https://www.themoviedb.org/tv/$serieId/reviews?language=es-ES"
                            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                        },
                        onRecommendationClick = { serie ->
                            viewModel.establecerItemSeleccionado(serie)
                        }
                    )
                }
            }
        }
    }
}
