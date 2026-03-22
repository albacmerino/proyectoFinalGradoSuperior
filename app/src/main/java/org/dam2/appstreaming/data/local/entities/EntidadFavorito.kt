package org.dam2.appstreaming.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla local para almacenar los favoritos del usuario. Es el objeto  que guaradará Room
 * en la bbdd de Android
 */
@Entity(tableName = "favoritos")
data class EntidadFavorito(

    @PrimaryKey val idMultimedia: Int,
    val titulo: String,
    val rutaPoster: String?,
    val esPelicula: Boolean // true para pelicula, false para serie
)
