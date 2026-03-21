package org.dam2.appstreaming.ui.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.dam2.appstreaming.R
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.MovieCard
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.SerieCard
import org.dam2.appstreaming.ui.component.Genero
import org.dam2.appstreaming.ui.screen.pelicula.PeliculaViewModel
import org.dam2.appstreaming.ui.screen.serie.SerieViewModel

/**
 * Fragmento principal de la aplicación.
 * Gestiona el catálogo de películas y series mediante una arquitectura MVVM.
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val peliculaViewModel: PeliculaViewModel by activityViewModels()
    private val serieViewModel: SerieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Observamos la pestaña actual (0: Pelis, 1: Series)
                val selectedTab by viewModel.selectedTab.collectAsState()

                // Datos de Películas
                val moviesNow by viewModel.movies.collectAsState(initial = emptyList())
                val moviesPop by viewModel.popularMovies.collectAsState(initial = emptyList())
                val moviesTop by viewModel.topRatedMovies.collectAsState(initial = emptyList())

                // Datos de Series (Aseguramos observación activa)
                val seriesNow by viewModel.series.collectAsState(initial = emptyList())
                val seriesPop by viewModel.popularSeries.collectAsState(initial = emptyList())
                val seriesTop by viewModel.topRatedSeries.collectAsState(initial = emptyList())

                // Catálogo de géneros y filtro actual
                val movieGenres by viewModel.movieGenres.collectAsState(initial = emptyList())
                val tvGenres by viewModel.tvGenres.collectAsState(initial = emptyList())
                val selectedGenreId by viewModel.selectedGenreId.collectAsState()

                MaterialTheme {
                    val seaBackgroundGradient = Brush.verticalGradient(
                        colors = listOf(Color(0xFF001D3D), Color(0xFF000814))
                    )
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = seaBackgroundGradient)
                        ) {
                            HomeScreen(
                                selectedTab = selectedTab,
                                onTabSelected = { viewModel.onTabSelected(it) },
                                moviesNow = moviesNow ?: emptyList(),
                                moviesPop = moviesPop ?: emptyList(),
                                moviesTop = moviesTop ?: emptyList(),
                                seriesNow = seriesNow ?: emptyList(),
                                seriesPop = seriesPop ?: emptyList(),
                                seriesTop = seriesTop ?: emptyList(),
                                // Pasamos los géneros según la pestaña
                                generos = (if (selectedTab == 0) movieGenres else tvGenres) ?: emptyList(),
                                selectedGenreId = selectedGenreId,
                                onGeneroClick = { viewModel.onGeneroSelected(it) },
                                onMovieClick = { movie ->
                                    peliculaViewModel.setSelectedItem(movie)
                                    peliculaViewModel.setGenres(movieGenres)
                                    findNavController().navigate(R.id.action_homeFragment_to_peliculaDetailFragment)
                                },
                                onSerieClick = { serie ->
                                    serieViewModel.setSelectedItem(serie)
                                    serieViewModel.setGenres(tvGenres)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    moviesNow: List<FichaPelicula>,
    moviesPop: List<FichaPelicula>,
    moviesTop: List<FichaPelicula>,
    seriesNow: List<FichaSerie>,
    seriesPop: List<FichaSerie>,
    seriesTop: List<FichaSerie>,
    generos: List<Genero>,
    selectedGenreId: Int?,
    onGeneroClick: (Int?) -> Unit,
    onMovieClick: (FichaPelicula) -> Unit,
    onSerieClick: (FichaSerie) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF000B1A),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text("SeaStream Menu", modifier = Modifier.padding(16.dp), color = SeaBlueLight, fontWeight = FontWeight.Bold)
                HorizontalDivider(color = SeaBlueLight.copy(alpha = 0.2f))
                NavigationDrawerItem(label = { Text("Perfil", color = Color.White) }, selected = false, onClick = {}, colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
                NavigationDrawerItem(label = { Text("Favoritos", color = Color.White) }, selected = false, onClick = {}, colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("SeaStream", style = MaterialTheme.typography.titleLarge.copy(brush = SeaGradient, fontWeight = FontWeight.ExtraBold)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = SeaBlueLight)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) { Icon(Icons.Default.Search, contentDescription = null, tint = SeaBlueLight) }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Selector de Películas/Series
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = SeaBlueLight,
                    indicator = { tabPositions ->
                        Box(Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(3.dp).background(brush = SeaGradient))
                    },
                    divider = {}
                ) {
                    Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) }, text = { Text("PELÍCULAS", color = if(selectedTab == 0) Color.White else Color.Gray) })
                    Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) }, text = { Text("SERIES", color = if(selectedTab == 1) Color.White else Color.Gray) })
                }

                // Selector de Géneros
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedGenreId == null,
                            onClick = { onGeneroClick(null) },
                            label = { Text("Todo") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SeaBlueLight, selectedLabelColor = Color.Black, labelColor = Color.LightGray)
                        )
                    }
                    items(generos) { genero ->
                        FilterChip(
                            selected = selectedGenreId == genero.id,
                            onClick = { onGeneroClick(genero.id) },
                            label = { Text(genero.name) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SeaBlueLight, selectedLabelColor = Color.Black, labelColor = Color.LightGray)
                        )
                    }
                }

                // Catálogo Dinámico
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 16.dp)) {
                    if (selectedTab == 0) {
                        // Usa 'generos', que es el parámetro que recibe la función HomeScreen
                        item { MovieSection("Novedades", moviesNow, generos, onMovieClick) }
                        item { MovieSection("Populares", moviesPop, generos, onMovieClick) }
                        item { MovieSection("Mejor valoradas", moviesTop, generos, onMovieClick) }
                    } else {
                        // Aquí también usa 'generos'
                        item { SerieSection("Novedades TV", seriesNow, generos, onSerieClick) }
                        item { SerieSection("Populares en TV", seriesPop, generos, onSerieClick) }
                        item { SerieSection("Mejor valoradas", seriesTop, generos, onSerieClick) }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieSection(title: String, movies: List<FichaPelicula>, allGenres: List<Genero>, onMovieClick: (FichaPelicula) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(movies) { movie ->
                MovieCard(movie = movie, allGenres = allGenres, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
fun SerieSection(title: String, series: List<FichaSerie>, allGenres: List<Genero>, onSerieClick: (FichaSerie) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(series) { serie ->
                SerieCard(serie = serie, allGenres = allGenres, onClick = { onSerieClick(serie) })
            }
        }
    }
}