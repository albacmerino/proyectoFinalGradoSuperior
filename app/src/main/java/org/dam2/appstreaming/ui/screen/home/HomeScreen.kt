package org.dam2.appstreaming.ui.screen.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient
import org.dam2.appstreaming.ui.component.*
import org.dam2.appstreaming.data.model.*

/**
 * Pantalla principal de la interfaz de usuario.
 * Define la estructura visual de la pantalla de inicio (Películas/Series).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedTab: Int,           // Pestaña seleccionada (0: Películas, 1: Series)
    onTabSelected: (Int) -> Unit, // Callback para cambiar de pestaña
    moviesNow: List<FichaPelicula>, // Listas de datos para las secciones
    moviesPop: List<FichaPelicula>,
    moviesTop: List<FichaPelicula>,
    seriesNow: List<FichaSerie>,
    seriesPop: List<FichaSerie>,
    seriesTop: List<FichaSerie>,
    generos: List<Genero>,       // Géneros para el selector
    selectedGenreId: Int?,       // Género actualmente filtrado
    onGeneroClick: (Int?) -> Unit, // Callback al elegir un género
    onMovieClick: (FichaPelicula) -> Unit, // Callback al pulsar una película
    onSerieClick: (FichaSerie) -> Unit      // Callback al pulsar una serie
) {
    // Estado para gestionar si el menú lateral está abierto
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Ámbito de corrutina para controlar el menú lateral (apertura/cierre animados)
    val scope = rememberCoroutineScope()

    // Envoltorio para el menú lateral de navegación
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer()
        } // El contenido del menú (Perfil, Favoritos...)
    ) {
        // Estructura básica de la pantalla (AppBar y contenido principal)
        Scaffold(
            topBar = {
                // Barra superior con botón de menú lateral y título
                HomeTopBar(onOpenMenu = {
                    scope.launch {
                        drawerState.open()
                    }
                })
            },
            containerColor = Color.Transparent // El fondo lo gestiona el Fragment

        ) { padding ->
            // Columna principal que organiza el contenido debajo de la AppBar
            Column(modifier = Modifier.padding(padding)) {
                
                // Pestañas superiores (PELÍCULAS / SERIES)
                HomeTabs(selectedTab, onTabSelected)
                
                // Filtros de géneros horizontales
                GenreSelector(generos, selectedGenreId, onGeneroClick)

                // El contenido principal desplazable (las listas de películas/series)
                MainContent(
                    selectedTab, moviesNow, moviesPop, moviesTop,
                    seriesNow, seriesPop, seriesTop, generos,
                    onMovieClick, onSerieClick
                )
            }
        }
    }
}

/**
 * Contenido del menú lateral de navegación.
 */
@Composable
private fun HomeDrawer() {

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF000B1A), // Color azul muy oscuro
        modifier = Modifier.width(300.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("SeaStream Menu", modifier = Modifier.padding(16.dp), color = SeaBlueLight, fontWeight = FontWeight.Bold)
        HorizontalDivider(color = SeaBlueLight.copy(alpha = 0.2f))
        NavigationDrawerItem(label = {
            Text("Perfil",
                color = Color.White) }, selected = false, onClick = {})
        NavigationDrawerItem(label = {
            Text("Favoritos", color = Color.White) }, selected = false, onClick = {})
    }
}

/**
 * Barra superior de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onOpenMenu: () -> Unit) {

    CenterAlignedTopAppBar(
        title = {
            Text("SeaStream",
                style = MaterialTheme.typography.titleLarge.copy(brush = SeaGradient,
                    fontWeight = FontWeight.ExtraBold))
                },

        navigationIcon = {
            IconButton(onClick = onOpenMenu) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = SeaBlueLight)
            }
        },
        actions = {
            IconButton(onClick = { }) { Icon(Icons.Default.Search, contentDescription = null, tint = SeaBlueLight) }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}

/**
 * Fila de pestañas para alternar entre Películas y Series.
 */
@Composable
private fun HomeTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = SeaBlueLight,
        indicator = { tabPositions ->
            // Línea indicadora personalizada con gradiente
            Box(Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(3.dp).background(brush = SeaGradient))
        },
        divider = {}
    ) {
        Tab(selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("PELÍCULAS",
                color = if(selectedTab == 0) Color.White else Color.Gray) })

        Tab(selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("SERIES",
                color = if(selectedTab == 1) Color.White else Color.Gray) })
    }
}

/**
 * Fila horizontal de botones de género (Chips).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreSelector(generos: List<Genero>, selectedId: Int?, onClick: (Int?) -> Unit) {

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Opción predeterminada: Todo
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onClick(null) },
                label = { Text("Todo") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SeaBlueLight, selectedLabelColor = Color.Black)
            )
        }
        // Lista dinámica de géneros
        items(generos) { genero ->
            FilterChip(
                selected = selectedId == genero.id,
                onClick = { onClick(genero.id) },
                label = { Text(genero.name) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SeaBlueLight, selectedLabelColor = Color.Black)
            )
        }
    }
}

/**
 * Organiza las secciones (Novedades, Populares, etc.) en una lista vertical optimizada.
 */
@Composable
private fun MainContent(
    selectedTab: Int,
    moviesNow: List<FichaPelicula>,
    moviesPop: List<FichaPelicula>,
    moviesTop: List<FichaPelicula>,
    seriesNow: List<FichaSerie>,
    seriesPop: List<FichaSerie>,
    seriesTop: List<FichaSerie>,
    generos: List<Genero>,
    onMovieClick: (FichaPelicula) -> Unit, onSerieClick: (FichaSerie) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 16.dp)) {
        if (selectedTab == 0) {
            // Muestra secciones de cine
            item { HomeSection(
                "Novedades", moviesNow, generos, onMovieClick, isMovie = true)
            }
            item { HomeSection(
                "Populares", moviesPop, generos, onMovieClick, isMovie = true)
            }
            item { HomeSection(
                "Mejor valoradas", moviesTop, generos, onMovieClick, isMovie = true)
            }
        } else {
            // Muestra secciones de televisión
            item { HomeSection(
                "Novedades TV", seriesNow, generos, onSerieClick, isMovie = false)
            }
            item { HomeSection(
                "Populares en TV", seriesPop, generos, onSerieClick, isMovie = false)
            }
            item { HomeSection(
                "Mejor valoradas", seriesTop, generos, onSerieClick, isMovie = false)
            }
        }
    }
}

/**
 * Una sección individual compuesta por un título y una lista horizontal de tarjetas.
 */
@Composable
private fun <T> HomeSection(
    title: String,
    items: List<T>,
    allGenres: List<Genero>,
    onClick: (T) -> Unit,
    isMovie: Boolean
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        // Carrusel horizontal de tarjetas
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            items(items) { item ->
                if (isMovie) {
                    MovieCard(movie = item as FichaPelicula, allGenres = allGenres, onClick = { onClick(item) })
                } else {
                    SerieCard(serie = item as FichaSerie, allGenres = allGenres, onClick = { onClick(item) })
                }
            }
        }
    }
}
