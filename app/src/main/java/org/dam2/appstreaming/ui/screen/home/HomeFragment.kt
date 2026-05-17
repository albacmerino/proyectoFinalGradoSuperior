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
import com.google.firebase.auth.FirebaseAuth
import org.dam2.appstreaming.R
import org.dam2.appstreaming.data.local.prefs.PreferenciasUsuario
import org.dam2.appstreaming.data.model.FichaPelicula
import org.dam2.appstreaming.data.model.FichaSerie
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

/**
 * FRAGMENTO PRINCIPAL
 * 
 * Actúa como el contenedor de la pantalla de inicio (HomeScreen). 
 * Se encarga de la interoperabilidad entre el sistema de Fragmentos tradicional y Jetpack Compose.
 *
 */
class HomeFragment : Fragment() {

    // ViewModel específico de la pantalla de inicio
    private val viewModel: HomeViewModel by viewModels()

    // ViewModels compartidos para pasar datos a las pantallas de detalle sin duplicar peticiones de red
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Observación de flujos de estado del ViewModel
                val estado by viewModel.estado.collectAsState()
                val idsFavoritos by viewModel.idsFavoritos.collectAsState()

                MaterialTheme {
                    val gradienteFondoMar = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = gradienteFondoMar)
                        ) {
                            HomeScreen(
                                selectedTab = estado.pestana,
                                onTabSelected = {
                                    viewModel.alCambiarPestana(it)
                                                },
                                moviesNow = estado.peliculasEstreno,
                                moviesPop = estado.peliculasPopulares,
                                moviesTop = estado.peliculasMejorValoradas,
                                seriesNow = estado.seriesEstreno,
                                seriesPop = estado.seriesPopulares,
                                seriesTop = estado.seriesMejorValoradas,
                                generos = estado.generosActuales,
                                selectedGenreId = estado.idGeneroSeleccionado,
                                onGeneroClick = {
                                    viewModel.alSeleccionarGenero(it)
                                                },
                                onMovieClick = { pelicula ->
                                    // Inyecta la película seleccionada en el ViewModel compartido antes de navegar
                                    peliculaViewModel.setSelectedItem(pelicula)
                                    peliculaViewModel.setGenres(estado.generosPelicula)
                                    findNavController().navigate(R.id.action_homeFragment_to_peliculaDetailFragment)
                                },
                                onSerieClick = { serie ->
                                    serieViewModel.establecerItemSeleccionado(serie)
                                    serieViewModel.setGeneros(estado.generosTv)
                                    findNavController().navigate(R.id.action_homeFragment_to_serieDetailFragment)
                                },
                                onNavigateToFavoritos = {
                                    findNavController().navigate(R.id.action_homeFragment_to_favoritosFragment)
                                },
                                onNavigateToMisListas = {
                                    findNavController().navigate(R.id.action_global_to_misListas)
                                },
                                idsFavoritos = idsFavoritos,
                                onToggleFavorite = { item ->
                                    // Gestiona favoritos según el tipo de contenido
                                    if (item is FichaPelicula) viewModel.toggleFavorito(item)
                                    else if (item is FichaSerie) viewModel.toggleFavoritoSerie(item)
                                },
                                onLogoutClick = {
                                    // Gestión completa de cierre de sesión: Firebase + Persistencia de preferencias
                                    FirebaseAuth.getInstance().signOut()
                                    val prefs = PreferenciasUsuario(requireContext())
                                    prefs.guardarMantenerSesion(false)
                                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                },
                                onLoadMore = { seccion ->
                                    viewModel.cargarMasContenido(seccion)
                                },
                                resultadosFiltroGenero = estado.resultadosFiltroGenero,
                                onCargarMasFiltro = { viewModel.cargarMasFiltroGenero() },onSearchClick = {

                                    findNavController().navigate(R.id.action_global_to_busqueda)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
