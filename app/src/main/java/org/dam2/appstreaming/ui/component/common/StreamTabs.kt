package org.dam2.appstreaming.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dam2.appstreaming.ui.colors.SeaGradient

/**
 * COMPONENTE: SELECTOR DE PESTAÑAS
 * 
 * Implementa una barra de navegación por pestañas personalizada para alternar entre "Películas" y "Series".
 *
 */
@Composable
fun StreamTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent, // Fondo transparente para integrar con el gradiente de la App
        indicator = { tabPositions ->
            // Indicador personalizado con el gradiente del proyecto
            Box(Modifier
                .tabIndicatorOffset(tabPositions[selectedTab])
                .height(3.dp)
                .background(brush = SeaGradient))
        },
        divider = {}
    ) {
        // Pestaña de Películas
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { 
                Text(
                    text = "PELÍCULAS", 
                    color = if(selectedTab == 0) Color.White else Color.Gray 
                ) 
            }
        )
        // Pestaña de Series
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { 
                Text(
                    text = "SERIES", 
                    color = if(selectedTab == 1) Color.White else Color.Gray 
                ) 
            }
        )
    }
}
