package org.dam2.appstreaming.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.dam2.appstreaming.data.local.entities.EntidadFavorito

/**
 * Operaciones de base de datos para los favoritos.
 */
@Dao
interface DaoFavorito {

    @Query("SELECT * FROM favoritos")
    fun obtenerTodosLosFavoritos(): Flow<List<EntidadFavorito>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFavorito(favorito: EntidadFavorito)

    @Delete
    suspend fun eliminarFavorito(favorito: EntidadFavorito)

    @Query("SELECT EXISTS(SELECT * FROM favoritos WHERE idMultimedia = :id)")
    fun esFavorito(id: Int): Flow<Boolean>
}
