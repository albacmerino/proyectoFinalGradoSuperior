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
import androidx.compose.runtime.*
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

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val peliculas by viewModel.movies.collectAsState()

                MaterialTheme {
                    val seaBackgroundGradient = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF001D3D), // Azul marino superior
                            Color(0xFF000814)  // Azul marino profundo
                        )
                    )
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = seaBackgroundGradient)
                        ) {
                            HomeScreen(peliculas)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(peliculas: List<FichaPelicula>) {
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
                NavigationDrawerItem(
                    label = { Text("Ver más tarde", color = Color.White) },
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
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item { MovieSection("Novedades", peliculas) }
                item { MovieSection("Ver más tarde", peliculas) }
                item { MovieSection("Podría gustarte", peliculas) }
                item { MovieSection("Tendencias Mundiales", peliculas) }
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
                MovieCard(movie)
            }
        }
    }
}