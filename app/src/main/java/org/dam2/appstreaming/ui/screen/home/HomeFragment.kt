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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.MovieCard
import org.dam2.appstreaming.ui.component.FichaPelicula
import org.dam2.appstreaming.ui.component.FichaSerie
import org.dam2.appstreaming.ui.component.SerieCard
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val selectedTab by viewModel.selectedTab.collectAsState()

// Películas
                val moviesNow by viewModel.movies.collectAsState() // Novedades
                val moviesPop by viewModel.popularMovies.collectAsState()
                val moviesTop by viewModel.topRatedMovies.collectAsState()

// Series
                val seriesNow by viewModel.series.collectAsState() // Novedades
                val seriesPop by viewModel.popularSeries.collectAsState()
                val seriesTop by viewModel.topRatedSeries.collectAsState()


                MaterialTheme {
                    val seaBackgroundGradient = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF001D3D),
                            Color(0xFF000814)
                        )
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
                                moviesNow = moviesNow,
                                moviesPop = moviesPop,
                                moviesTop = moviesTop,
                                seriesNow = seriesNow,
                                seriesPop = seriesPop,
                                seriesTop = seriesTop)
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
    seriesTop: List<FichaSerie>
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
                Text(
                    text = "SeaStream Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = SeaBlueLight,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(color = SeaBlueLight.copy(alpha = 0.2f))

                NavigationDrawerItem(
                    label = { Text("Perfil", color = Color.White) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("Favoritos", color = Color.White) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "SeaStream",
                            style = MaterialTheme.typography.titleLarge.copy(
                                brush = SeaGradient,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = SeaBlueLight)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Navegar a búsqueda */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = SeaBlueLight)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {

                // --- SELECTOR NEÓN (PELÍCULAS VS SERIES) ---
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = SeaBlueLight,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .background(brush = SeaGradient)
                            )
                        }
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { onTabSelected(0) },
                        text = {
                            Text(
                                "PELÍCULAS",
                                color = if(selectedTab == 0) Color.White else Color.Gray,
                                fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        text = {
                            Text(
                                "SERIES",
                                color = if(selectedTab == 1) Color.White else Color.Gray,
                                fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                // --- LISTADO DINÁMICO SEGÚN LA PESTAÑA SELECCIONADA ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    if (selectedTab == 0) {
                        // SECCIONES DE PELÍCULAS
                        item { MovieSection("Novedades en Cine", moviesNow) }
                        item { MovieSection("Populares ahora", moviesPop) }
                        item { MovieSection("Mejor valoradas", moviesTop) }
                    } else {
                        // SECCIONES DE SERIES
                        item { SerieSection("Novedades en TV", seriesNow) }
                        item { SerieSection("Series del Momento", seriesPop) }
                        item { SerieSection("Mejor valoradas", seriesTop) }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieSection(title: String, movies: List<FichaPelicula>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie)
            }
        }
    }
}

@Composable
fun SerieSection(title: String, series: List<FichaSerie>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(series) { serie ->
                SerieCard(serie = serie)
            }
        }
    }
}