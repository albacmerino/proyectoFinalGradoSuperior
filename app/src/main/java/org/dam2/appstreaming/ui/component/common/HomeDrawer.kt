package org.dam2.appstreaming.ui.component.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dam2.appstreaming.ui.colors.SeaBlueLight

@Composable
fun HomeDrawer(
    onHomeClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onFavoritosClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        // Cambiado 'containerColor' por 'drawerContainerColor' según el error
        drawerContainerColor = Color(0xFF000B1A),
        modifier = Modifier.width(300.dp)
    ) {
        // --- 1. CABECERA (SeaStream Menu en grande) ---
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "SeaStream Menu",
            modifier = Modifier.padding(16.dp),
            color = SeaBlueLight,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = SeaBlueLight.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. BOTONES DE NAVEGACIÓN ---

        // BOTÓN INICIO
        NavigationDrawerItem(
            label = { Text("Inicio", color = Color.White) },
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = SeaBlueLight) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        // BOTÓN PERFIL
        NavigationDrawerItem(
            label = { Text("Perfil", color = Color.White) },
            selected = false,
            onClick = onPerfilClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = SeaBlueLight) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        // BOTÓN FAVORITOS
        NavigationDrawerItem(
            label = { Text("Favoritos", color = Color.White) },
            selected = false,
            onClick = onFavoritosClick,
            icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = SeaBlueLight) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        // Empuja el botón de logout hacia la parte inferior
        // (Ya no usa el import de wear, usa el de foundation.layout incluido en ColumnScope)
        Spacer(modifier = Modifier.weight(1f))

        // --- 3. BOTÓN CERRAR SESIÓN ---
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color(0xFFFF5252)) },
            selected = false,
            onClick = onLogoutClick,
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252)) },
            modifier = Modifier.padding(bottom = 16.dp),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )
    }
}