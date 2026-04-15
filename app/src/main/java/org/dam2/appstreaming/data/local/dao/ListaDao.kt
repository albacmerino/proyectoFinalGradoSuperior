package org.dam2.appstreaming.data.local.dao

import androidx.room.*
import org.dam2.appstreaming.data.local.entities.ListaEntity

@Dao
interface ListaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: ListaEntity)

    @Query("SELECT DISTINCT tipoLista FROM listas_locales WHERE usuarioId = :uId")
    fun obtenerNombresDeMisListas(uId: String): kotlinx.coroutines.flow.Flow<List<String>> // Cambiado de suspend a Flow

    @Query("SELECT * FROM listas_locales WHERE usuarioId = :uId")
    fun obtenerPorUsuario(uId: String): kotlinx.coroutines.flow.Flow<List<ListaEntity>>
    @Query("SELECT * FROM listas_locales WHERE usuarioId = :uId AND tipoLista = :tipo")
    suspend fun obtenerPorTipo(uId: String, tipo: String): List<ListaEntity>

    @Query("DELETE FROM listas_locales WHERE usuarioId = :uId AND idMultimedia = :mId AND tipoLista = :tipo")
    suspend fun eliminar(uId: String, mId: Int, tipo: String)

    @Query("SELECT EXISTS(SELECT 1 FROM listas_locales WHERE usuarioId = :uId AND idMultimedia = :mId AND tipoLista = :tipo)")
    suspend fun existe(uId: String, mId: Int, tipo: String): Boolean

    @Query("SELECT idMultimedia FROM listas_locales WHERE usuarioId = :uId AND tipoLista = 'FAVORITO'")
    fun obtenerIdsFavoritos(uId: String): kotlinx.coroutines.flow.Flow<List<Int>>
}