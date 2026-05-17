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
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.repository.RepositorioBackend

/**
 * DETALLE DE PELÍCULA
 * 
 * Actúa como el host de Android para la pantalla de detalles desarrollada en Compose.
 * Se encarga de gestionar el ciclo de vida y la integración con componentes del sistema (Intents, Navegación).
 *
 */
class PeliculaDetailFragment : Fragment() {

    // Recuperamos el ViewModel para compartir datos entre pantallas
    private val viewModel: PeliculaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización del repositorio de backend
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
                    PeliculaDetailScreen(
                        state = state,
                        onBackClick = { 
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
                        onSeeAllReviewsClick = {
                            val bundle = Bundle().apply {
                                putString("mediaType", "pelicula")
                            }
                            findNavController().navigate(R.id.action_peliculaDetailFragment_to_reviewsFragment, bundle)
                        },
                        onRecommendationClick = { movie ->
                            // Al pulsar una recomendación, actualizamos el ViewModel con la nueva película
                            viewModel.setSelectedItem(movie)
                        },
                        onAddClick = { pelicula, nombreLista ->
                            if (nombreLista == null) {
                                viewModel.abrirSheet()
                            } else {
                                viewModel.guardarEnLista(
                                    id = pelicula.id,
                                    titulo = pelicula.titulo,
                                    rutaPoster = pelicula.rutaPoster,
                                    esPelicula = true,
                                    nombreLista = nombreLista
                                )
                            }
                        },
                        onCloseSheet = { viewModel.cerrarSheet() }
                    )
                }
            }
        }
    }
}
