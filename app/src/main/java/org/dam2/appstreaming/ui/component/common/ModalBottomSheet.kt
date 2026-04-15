package org.dam2.appstreaming.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToListSheet(
    listasExistentes: List<String>,
    onNombreNuevaLista: (String) -> Unit,
    onListaSeleccionada: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nuevoNombre by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF001D3D) // Azul oscuro como tu app
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text("Añadir a una lista", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO PARA NUEVA LISTA
            OutlinedTextField(
                value = nuevoNombre,
                onValueChange = { nuevoNombre = it },
                label = { Text("Nueva lista...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                trailingIcon = {
                    if (nuevoNombre.isNotBlank()) {
                        IconButton(onClick = {
                            onNombreNuevaLista(nuevoNombre)
                            nuevoNombre = ""
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Cyan)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Tus listas actuales:", color = Color.Gray, fontSize = 14.sp)

            // LISTADO DE EXISTENTES
            LazyColumn {
                items(listasExistentes) { nombre ->
                    ListItem(
                        headlineContent = { Text(nombre, color = Color.White) },
                        modifier = Modifier.clickable { onListaSeleccionada(nombre) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = { Icon(Icons.Default.List, contentDescription = null, tint = Color.Cyan) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}