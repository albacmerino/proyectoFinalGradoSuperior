package org.dam2.appstreaming.data.local.dao

import androidx.room.*
import org.dam2.appstreaming.data.local.entities.ListaEntity

/**
 * DATA ACCESS OBJECT: LISTA DAO
 *
 * Define las operaciones de acceso a datos para la tabla de listas locales.
 * Room utiliza esta interfaz para generar la implementación de las consultas SQL de forma segura.
 *
 * - Programación Reactiva: Uso de Kotlin Flow para emitir cambios en tiempo real a la UI.
 * - Operaciones Asíncronas: Uso de 'suspend functions' para garantizar que las operaciones de E/S
 *   no bloqueen el hilo de interfaz de usuario.
 */
@Dao
interface ListaDao {
    /**
     * Inserta un elemento en la lista. Si ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: ListaEntity)

    /**
     * Obtiene los nombres únicos de las categorías de listas creadas por un usuario.
     * Devuelve un Flow para que la UI se actualice automáticamente al crear nuevas listas.
     */
    @Query("SELECT DISTINCT tipoLista FROM listas_locales WHERE usuarioId = :uId")
    fun obtenerNombresDeMisListas(uId: String): kotlinx.coroutines.flow.Flow<List<String>>

    /**
     * Obtiene todos los registros asociados a un usuario específico.
     */
    @Query("SELECT * FROM listas_locales WHERE usuarioId = :uId")
    fun obtenerPorUsuario(uId: String): kotlinx.coroutines.flow.Flow<List<ListaEntity>>

    /**
     * Consulta para obtener los elementos de una categoría concreta.
     */
    @Query("SELECT * FROM listas_locales WHERE usuarioId = :uId AND tipoLista = :tipo")
    suspend fun obtenerPorTipo(uId: String, tipo: String): List<ListaEntity>

    /**
     * Elimina un elemento específico de una lista basándose en su ID de TMDB y su tipo.
     */
    @Query("DELETE FROM listas_locales WHERE usuarioId = :uId AND idMultimedia = :mId AND tipoLista = :tipo")
    suspend fun eliminar(uId: String, mId: Int, tipo: String)

    /**
     * Verifica la existencia de un elemento en una lista específica.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM listas_locales WHERE usuarioId = :uId AND idMultimedia = :mId AND tipoLista = :tipo)")
    suspend fun existe(uId: String, mId: Int, tipo: String): Boolean

    /**
     * Expone de forma reactiva los IDs de los elementos marcados como favoritos.
     * Crucial para el estado visual de los iconos de favorito en el catálogo.
     */
    @Query("SELECT idMultimedia FROM listas_locales WHERE usuarioId = :uId AND tipoLista = 'FAVORITO'")
    fun obtenerIdsFavoritos(uId: String): kotlinx.coroutines.flow.Flow<List<Int>>
}
