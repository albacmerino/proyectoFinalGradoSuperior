package org.dam2.appstreaming.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listas_locales")
data class ListaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idMultimedia: Int,
    val titulo: String,
    val rutaPoster: String?,
    val esPelicula: Boolean,
    val tipoLista: String,
    val usuarioId: String
)