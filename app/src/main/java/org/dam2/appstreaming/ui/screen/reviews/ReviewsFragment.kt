package org.dam2.appstreaming.ui.screen.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

/**
 * Fragmento que muestra la lista completa de reseñas para una película o serie.
 */
class ReviewsFragment : Fragment() {

    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    // Obtenemos el tipo de medio (película o serie) de los argumentos
                    val mediaType = arguments?.getString("mediaType") ?: "pelicula"
                    
                    if (mediaType == "pelicula") {
                        val state by peliculaViewModel.state.collectAsState()
                        ReviewsScreen(
                            title = state.movie?.titulo ?: "Película",
                            reviews = state.reviews,
                            onBackClick = {
                                findNavController().popBackStack()
                            }
                        )
                    } else {
                        val state by serieViewModel.state.collectAsState()
                        ReviewsScreen(
                            title = state.serie?.titulo ?: "Serie",
                            reviews = state.reviews,
                            onBackClick = {
                                findNavController().popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
