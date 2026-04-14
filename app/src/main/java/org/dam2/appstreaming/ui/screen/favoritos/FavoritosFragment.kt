package org.dam2.appstreaming.ui.screen.favoritos

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.dam2.appstreaming.data.mapper.toFichaPelicula
import org.dam2.appstreaming.data.mapper.toFichaSerie
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

class FavoritosFragment : Fragment() {

    private val viewModel: FavoritosViewModel by viewModels()

    // Necesitamos estos para pasar los datos antes de navegar al detalle
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val favoritos by viewModel.listaFavoritos.collectAsState()
                var tabSeleccionada by remember { mutableIntStateOf(0) }

                // Cargar favoritos de Room al entrar
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    viewModel.cargarFavoritos()
                }

                MaterialTheme {
                    val gradienteFondoMar = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )

                    Box(modifier = Modifier.fillMaxSize().background(brush = gradienteFondoMar)) {
                        FavoritosScreen(
                            selectedTab = tabSeleccionada,
                            onTabSelected = { tabSeleccionada = it },
                            listaContenido = favoritos,
                            onItemClick = { item ->
                                if (item.esPelicula) {
                                    // Mapeamos RespuestaLista a FichaPelicula para que el detalle lo entienda
                                    peliculaViewModel.setSelectedItem(item.toFichaPelicula())
                                    findNavController().navigate(R.id.action_favoritosFragment_to_peliculaDetailFragment)
                                } else {
                                    // Mapeamos RespuestaLista a FichaSerie
                                    serieViewModel.establecerItemSeleccionado(item.toFichaSerie())
                                    findNavController().navigate(R.id.action_favoritosFragment_to_serieDetailFragment)
                                }
                            },
                            onLogoutClick = {
                                FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.action_favoritosFragment_to_loginFragment)
                            },
                            onNavigateToHome = {
                                findNavController().navigate(R.id.homeFragment)
                            }
                        )
                    }
                }
            }
        }
    }
}