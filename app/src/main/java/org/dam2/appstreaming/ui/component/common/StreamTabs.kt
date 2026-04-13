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


@Composable
fun StreamTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            Box(Modifier
                .tabIndicatorOffset(tabPositions[selectedTab])
                .height(3.dp)
                .background(brush = SeaGradient))
        },
        divider = {}
    ) {
        Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) },
            text = { Text("PELÍCULAS", color = if(selectedTab == 0) Color.White else Color.Gray) })
        Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) },
            text = { Text("SERIES", color = if(selectedTab == 1) Color.White else Color.Gray) })
    }
}