package org.dam2.appstreaming.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.dam2.appstreaming.data.local.AppDatabase
import org.dam2.appstreaming.data.local.entities.ListaEntity
import org.dam2.appstreaming.data.local.prefs.GestorToken
import org.dam2.appstreaming.data.remote.dto.backend.RespuestaLista
import org.dam2.appstreaming.data.remote.dto.backend.SolicitudLista

/**
 * REPOSITORIO BACKEND (PERSISTENCIA LOCAL Y SINCRONIZACIÓN)
 * 
 * Esta clase gestiona la lógica de persistencia para las listas personalizadas y favoritos del usuario.
 * Actualmente utiliza Room como motor de BBDD local para garantizar el funcionamiento offline
 * y la rapidez en la consulta de datos.
 *
 */
class RepositorioBackend(contexto: Context) {

    // Instancia de la BBDD local
    private val database = AppDatabase.getDatabase(contexto)
    private val listaDao = database.listaDao()
    
    // Gestor de preferencias para almacenamiento simple de tokens/sesión
    private val gestorToken = GestorToken(contexto)

    /**
     * Sincroniza los datos del usuario tras la autenticación.
     */
    fun sincronizarUsuario(uid: String, nombre: String): Boolean {
        Log.d("RepositorioBackend", "Sincronización local completada para $nombre")
        gestorToken.guardarToken(uid)
        return true
    }

    /**
     * Obtiene el contenido de una lista específica (ej. "FAVORITO") para el usuario actual.
     * Realiza un mapeo de Entity (Room) a DTO de respuesta para la UI.
     */
    suspend fun obtenerContenidoLista(tipoLista: String): List<RespuestaLista> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return listaDao.obtenerPorTipo(uid, tipoLista).map {
            RespuestaLista(it.idMultimedia, it.titulo, it.rutaPoster, it.esPelicula, it.tipoLista)
        }
    }

    /**
     * Elimina un elemento de una lista local.
     */
    suspend fun eliminarDeLista(tipoLista: String, idMultimedia: Int): Boolean {
        return try {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            listaDao.eliminar(uid, idMultimedia, tipoLista)
            true
        } catch (_: Exception) { false }
    }

    /**
     * Devuelve un Flow reactivo con los IDs de los elementos marcados como favoritos.
     */
    fun obtenerIdsFavoritos(): Flow<List<Int>> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return listaDao.obtenerIdsFavoritos(uid)
    }

    /**
     * Agrega o elimina un elemento de una lista
     */
    suspend fun agregarALista(solicitud: SolicitudLista): Boolean {
        return try {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Verificamos existencia para decidir si insertar o borrar
            val existe = listaDao.existe(uid, solicitud.idMultimedia, solicitud.tipoLista)

            if (existe) {
                listaDao.eliminar(uid, solicitud.idMultimedia, solicitud.tipoLista)
                Log.d("ROOM", "Eliminado de lista local: ${solicitud.titulo}")
            } else {
                listaDao.insertar(ListaEntity(
                    idMultimedia = solicitud.idMultimedia,
                    titulo = solicitud.titulo,
                    rutaPoster = solicitud.rutaPoster,
                    esPelicula = solicitud.esPelicula,
                    tipoLista = solicitud.tipoLista,
                    usuarioId = uid
                ))
                Log.d("ROOM", "Añadido a lista local: ${solicitud.titulo}")
            }
            true
        } catch (e: Exception) {
            Log.e("ROOM", "Error en Room: ${e.message}")
            false
        }
    }

    /**
     * Obtiene los nombres únicos de las listas creadas por el usuario.
     */
    fun obtenerNombresDeListas(): Flow<List<String>> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return listaDao.obtenerNombresDeMisListas(uid).map { listas ->
            val resultado = listas.toMutableList()
            // Aseguramos que la lista FAVORITO siempre sea la primera
            if (!resultado.contains("FAVORITO")) resultado.add(0, "FAVORITO")
            resultado
        }
    }

    /**
     * Devuelve todas las listas del usuario agrupadas por su nombre.
     * Se utiliza en la pantalla de "Mis Listas" para mostrar carruseles por categoría.
     */
    fun obtenerTodasLasListasAgrupadas(): Flow<Map<String, List<RespuestaLista>>> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return listaDao.obtenerPorUsuario(uid).map { listaEntities ->
            listaEntities.map { entity ->
                RespuestaLista(
                    idMultimedia = entity.idMultimedia,
                    titulo = entity.titulo,
                    rutaPoster = entity.rutaPoster,
                    esPelicula = entity.esPelicula,
                    tipoLista = entity.tipoLista
                )
            }.groupBy { it.tipoLista }
        }
    }
}
