package org.dam2.appstreaming.ui.component.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.dam2.appstreaming.ui.colors.SeaBlueLight
import org.dam2.appstreaming.ui.colors.SeaGradient

/**
 * COMPONENTE: BARRA SUPERIOR PERSONALIZADA
 * 
 * Actúa como la cabecera global de la aplicación.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamTopBar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        actions = {
            // Acción de búsqueda accesible desde cualquier pantalla que use esta barra
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = SeaBlueLight
                )
            }
        },
        title = {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = SeaGradient, 
                    fontWeight = FontWeight.ExtraBold
                )
            )
        },
        navigationIcon = {
            // Navegación jerárquica (atrás) y lateral (menú)
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Volver", 
                        tint = SeaBlueLight
                    )
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu, 
                        contentDescription = "Menú lateral", 
                        tint = SeaBlueLight
                    )
                }
            }
        },
        // Mantenemos el fondo transparente para que se vea el gradiente de fondo de la App
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}
