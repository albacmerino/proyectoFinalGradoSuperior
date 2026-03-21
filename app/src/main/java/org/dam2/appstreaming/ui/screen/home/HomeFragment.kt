package org.dam2.appstreaming.ui.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

/**
 * Fragmento principal de la aplicación que actúa como contenedor para la UI de Compose.
 * Gestiona la comunicación entre los ViewModels y la navegación de Android Jetpack.
 */
class HomeFragment : Fragment() {

    // ViewModel específico de la Home, sobrevive solo mientras el fragmento está activo
    private val viewModel: HomeViewModel by viewModels()
    
    // ViewModels compartidos a nivel de Activity para pasar datos a las pantallas de detalle
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Retornamos una ComposeView, que nos permite usar Jetpack Compose dentro de un Fragment
        return ComposeView(requireContext()).apply {
            setContent {
                // Recolectamos el flujo de estado (Flow) del ViewModel. 
                // 'state' se actualizará automáticamente cuando cambien los datos en el ViewModel.
                val state by viewModel.state.collectAsState()

                MaterialTheme {
                    // Definimos un degradado vertical que simula la profundidad del mar
                    val seaBackgroundGradient = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )
                    // Superficie base que ocupa toda la pantalla
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // Caja contenedora para aplicar el fondo de degradado
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = seaBackgroundGradient)
                        ) {
                            // Invocamos la función Composable principal de la pantalla de inicio
                            HomeScreen(
                                selectedTab = state.tab, // Pestaña actual (Pelis/Series)
                                onTabSelected = { viewModel.onTab(it) }, // Acción al cambiar pestaña
                                moviesNow = state.moviesNow, // Datos de películas
                                moviesPop = state.moviesPop,
                                moviesTop = state.moviesTop,
                                seriesNow = state.seriesNow, // Datos de series
                                seriesPop = state.seriesPop,
                                seriesTop = state.seriesTop,
                                generos = state.currentGenres, // Géneros según la pestaña activa
                                selectedGenreId = state.selectedGenreId, // Género filtrado
                                onGeneroClick = { viewModel.onGenre(it) }, // Acción al filtrar
                                onMovieClick = { movie ->
                                    // Al hacer clic en una película, preparamos el ViewModel de detalle
                                    peliculaViewModel.setSelectedItem(movie)
                                    peliculaViewModel.setGenres(state.movieGenres)
                                    // Navegamos al fragmento de detalle de película
                                    findNavController().navigate(R.id.action_homeFragment_to_peliculaDetailFragment)
                                },
                                onSerieClick = { serie ->
                                    // Al hacer clic en una serie, preparamos el ViewModel de detalle
                                    serieViewModel.establecerItemSeleccionado(serie)
                                    serieViewModel.setGeneros(state.tvGenres)
                                    // Navegamos al fragmento de detalle de serie
                                    findNavController().navigate(R.id.action_homeFragment_to_serieDetailFragment)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
