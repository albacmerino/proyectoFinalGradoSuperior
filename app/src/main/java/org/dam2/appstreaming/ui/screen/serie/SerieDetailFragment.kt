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
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.repository.RepositorioBackend

/**
 * DETALLE DE SERIE
 * 
 * Actúa como host de la pantalla de detalles de series en Jetpack Compose.
 * Facilita la integración con el sistema de navegación de Android y la gestión de recursos del sistema.
 *
 */
class SerieDetailFragment : Fragment() {

    private val viewModel: SerieViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialización del repositorio inyectando el contexto necesario para Room
        val repo = RepositorioBackend(requireContext())
        viewModel.iniciarRepositorio(repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()

                MaterialTheme {
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
                        onSeeAllReviewsClick = {
                            val bundle = Bundle().apply {
                                putString("mediaType", "serie")
                            }
                            findNavController().navigate(R.id.action_serieDetailFragment_to_reviewsFragment, bundle)
                        },
                        onRecommendationClick = { serie ->
                            viewModel.establecerItemSeleccionado(serie)
                        },
                        onAddClick = { serie, nombreLista ->
                            if (nombreLista == null) {
                                viewModel.abrirSheet()
                            } else {
                                // Persistencia en Room mediante el ViewModel
                                viewModel.guardarEnLista(
                                    id = serie.id,
                                    titulo = serie.titulo,
                                    rutaPoster = serie.rutaPoster,
                                    esPelicula = false,
                                    nombreLista = nombreLista
                                )
                            }
                        },
                        onCloseSheet = {
                            viewModel.cerrarSheet()
                        }
                    )
                }
            }
        }
    }
}
