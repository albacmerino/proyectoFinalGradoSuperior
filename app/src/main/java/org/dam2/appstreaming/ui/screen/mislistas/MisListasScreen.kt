package org.dam2.appstreaming.ui.screen.mislistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista

/**
 * GESTIÓN DE MIS LISTAS
 * 
 * Interfaz desarrollada íntegramente con Jetpack Compose que permite al usuario 
 * visualizar y gestionar sus colecciones personalizadas de contenido multimedia.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisListasScreen(
    listasAgrupadas: Map<String, List<RespuestaLista>>,
    onItemClick: (RespuestaLista) -> Unit,
    onDeleteClick: (RespuestaLista) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            org.dam2.appstreaming.ui.component.common.HomeDrawer(
                onHomeClick = { scope.launch { drawerState.close(); onNavigateToHome() } },
                onFavoritosClick = { scope.launch { drawerState.close(); onNavigateToFavoritos() } },
                onMisListasClick = { scope.launch { drawerState.close() } },
                onLogoutClick = { scope.launch { drawerState.close(); onLogoutClick() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                // Barra superior modular configurable para cada sección
                org.dam2.appstreaming.ui.component.common.StreamTopBar(
                    title = "Mis Listas",
                    showBackButton = false,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            // Si no hay listas, informamos al usuario
            if (listasAgrupadas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no tienes listas creadas", color = Color.Gray)
                }
            } else {
                // Renderizado dinámico de secciones basado en las claves del mapa
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    listasAgrupadas.forEach { (nombre, contenido) ->
                        item {
                            SeccionPersonalizada(nombre, contenido, onItemClick, onDeleteClick)
                        }
                    }
                }
            }
        }
    }
}

/**
 * SECCIÓN POR CATEGORÍA
 * Implementa una fila horizontal de elementos multimedia con un encabezado descriptivo.
 */
@Composable
fun SeccionPersonalizada(
    titulo: String,
    items: List<RespuestaLista>,
    onClick: (RespuestaLista) -> Unit,
    onDeleteClick: (RespuestaLista) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            color = org.dam2.appstreaming.ui.colors.SeaBlueLight,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CardMiniatura(
                    item = item,
                    onClick = { onClick(item) },
                    onDelete = { onDeleteClick(item) }
                )
            }
        }
    }
}

/**
 * CARD DE GESTIÓN RÁPIDA
 * Visualización compacta optimizada para operaciones de borrado mediante iconos de acción superpuestos.
 */
@Composable
private fun CardMiniatura(
    item: RespuestaLista,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        // Integración con Coil para carga de recursos remotos
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w342${item.rutaPoster}",
            contentDescription = item.titulo,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Botón de borrado persistente con feedback visual de contraste
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
