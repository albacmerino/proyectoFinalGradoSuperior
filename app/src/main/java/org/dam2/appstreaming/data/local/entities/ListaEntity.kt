package org.dam2.appstreaming.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD DE BASE DE DATOS: LISTA LOCAL
 *
 * Representa la tabla "listas_locales" en Room. Esta clase define la estructura de persistencia
 * para los elementos guardados por el usuario (favoritos, ver más tarde, etc.).
 *
 * - Persistencia Relacional: Mapeo de atributos a columnas de una tabla relacional.
 */
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