package org.dam2.appstreaming.ui.screen.mislistas


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import org.dam2.appstreaming.data.mapper.toFichaPelicula
import org.dam2.appstreaming.data.mapper.toFichaSerie
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

class MisListasFragment : Fragment() {

    // ViewModel específico de esta pantalla
    private val viewModel: MisListasViewModel by viewModels()

    // ViewModels compartidos para preparar los datos de la pantalla de detalle
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Observamos el mapa de listas agrupadas (Nombre -> Lista de items)
                val listasAgrupadas by viewModel.listas.collectAsState()

                MaterialTheme {
                    // Aplicamos el fondo degradado azul marino constante en tu App
                    val gradienteFondoMar = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = gradienteFondoMar)
                    ) {
                        MisListasScreen(
                            listasAgrupadas = listasAgrupadas,
                            onItemClick = { item ->
                                // Lógica de navegación inteligente
                                if (item.esPelicula) {
                                    // Preparamos el ViewModel de Películas y navegamos
                                    peliculaViewModel.setSelectedItem(item.toFichaPelicula())
                                    findNavController().navigate(R.id.action_misListas_to_peliculaDetail)
                                } else {
                                    // Preparamos el ViewModel de Series y navegamos
                                    serieViewModel.establecerItemSeleccionado(item.toFichaSerie())
                                    findNavController().navigate(R.id.action_misListas_to_serieDetail)
                                }
                            },
                            onDeleteClick = { item -> viewModel.eliminarDeLista(item) },
                            onNavigateToHome = { findNavController().navigate(R.id.homeFragment) },
                            onNavigateToFavoritos = { findNavController().navigate(R.id.favoritosFragment) },
                            onLogoutClick = {
                                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.action_global_to_loginFragment) // Asegúrate de tener esta acción global
                            }
                        )
                    }
                }
            }
        }
    }
}