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
 * Gestiona la comunicacion entre los ViewModels y la navegacion de Android Jetpack.
 */
class HomeFragment : Fragment() {

    // ViewModel especifico de la Home, sobrevive solo mientras el fragmento está activo
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
                // 'estado' se actualizará automáticamente cuando cambien los datos en el ViewModel.
                val estado by viewModel.estado.collectAsState()

                MaterialTheme {
                    // Definimos un degradado vertical que simula la profundidad del mar
                    val gradienteFondoMar = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )
                    // Superficie base que ocupa toda la pantalla
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // Caja contenedora para aplicar el fondo de degradado
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = gradienteFondoMar)
                        ) {
                            // Invocamos la funcion Composable principal de la pantalla de inicio
                            HomeScreen(
                                selectedTab = estado.pestaña, // Pestaña actual (Pelis/Series)
                                onTabSelected = { viewModel.alCambiarPestaña(it) }, // Accion al cambiar pestaña
                                moviesNow = estado.peliculasEstreno, // Ahora usamos las listas directas del estado
                                moviesPop = estado.peliculasPopulares,
                                moviesTop = estado.peliculasMejorValoradas,
                                seriesNow = estado.seriesEstreno,
                                seriesPop = estado.seriesPopulares,
                                seriesTop = estado.seriesMejorValoradas,
                                generos = estado.generosActuales, // Generos según la pestaña activa
                                selectedGenreId = estado.idGeneroSeleccionado, // Genero filtrado
                                onGeneroClick = { viewModel.alSeleccionarGenero(it) }, // Accion al filtrar
                                onMovieClick = { pelicula ->
                                    // Al hacer clic en una pelicula, preparamos el ViewModel de detalle
                                    peliculaViewModel.setSelectedItem(pelicula)
                                    peliculaViewModel.setGenres(estado.generosPelicula)
                                    // Navegamos al fragmento de detalle de pelicula
                                    findNavController().navigate(R.id.action_homeFragment_to_peliculaDetailFragment)
                                },
                                onSerieClick = { serie ->
                                    // Al hacer clic en una serie, preparamos el ViewModel de detalle
                                    serieViewModel.establecerItemSeleccionado(serie)
                                    serieViewModel.setGeneros(estado.generosTv)
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
