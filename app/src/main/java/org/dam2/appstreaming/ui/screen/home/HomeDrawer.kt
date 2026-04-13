package org.dam2.appstreaming.ui.component.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.dam2.appstreaming.ui.colors.SeaBlueLight

@Composable
fun HomeDrawer(
    onPerfilClick: () -> Unit,
    onFavoritosClick: () -> Unit,
    onLogoutClick: () -> Unit // Pasamos la acción de cerrar sesión
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF000B1A),
        modifier = Modifier.width(300.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            "SeaStream Menu",
            modifier = Modifier.padding(16.dp),
            color = SeaBlueLight,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = SeaBlueLight.copy(alpha = 0.2f))

        NavigationDrawerItem(
            label = { Text("Perfil", color = Color.White) },
            selected = false,
            onClick = onPerfilClick
        )

        NavigationDrawerItem(
            label = { Text("Favoritos", color = Color.White) },
            selected = false,
            onClick = { onFavoritosClick() }, // Esta función navegará al nuevo fragmentoicon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = SeaBlueLight) }
        )

        // Empuja el botón de logout hacia la parte inferior
        Spacer(modifier = Modifier.weight(1f))

        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color(0xFFFF5252)) },
            selected = false,
            onClick = onLogoutClick,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}