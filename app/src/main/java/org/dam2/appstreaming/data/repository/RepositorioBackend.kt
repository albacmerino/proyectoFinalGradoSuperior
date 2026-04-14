package org.dam2.appstreaming.data.repository

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.dam2.appstreaming.data.local.AppDatabase
import org.dam2.appstreaming.data.local.entities.ListaEntity
import org.dam2.appstreaming.data.local.prefs.GestorToken
import org.dam2.appstreaming.data.remote.api.ServicioApiBackend
import org.dam2.appstreaming.data.remote.dto.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RepositorioBackend(contexto: Context) {

    private val database = AppDatabase.getDatabase(contexto)
    private val listaDao = database.listaDao()
    private val gestorToken = GestorToken(contexto)

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServicioApiBackend::class.java)

    // --- 1. SINCRONIZACIÓN (El nuevo "Login/Registro" para PostgreSQL) ---

    suspend fun sincronizarUsuario(uid: String, nombre: String, email: String?): Boolean {
        // Ya no llamamos a api.sincronizarUsuario(solicitud)
        // Simplemente devolvemos true para que la App avance al Home sin errores de red
        Log.d("RepositorioBackend", "Sincronización local completada para $nombre")
        gestorToken.guardarToken(uid)
        return true
    }

    // --- 2. GESTIÓN DE LISTAS (LOCAL CON ROOM) ---

    suspend fun obtenerContenidoLista(tipoLista: String): List<RespuestaLista> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return listaDao.obtenerPorTipo(uid, tipoLista).map {
            RespuestaLista(it.idMultimedia, it.titulo, it.rutaPoster, it.esPelicula, it.tipoLista)
        }
    }

    suspend fun eliminarDeLista(tipoLista: String, idMultimedia: Int): Boolean {
        return try {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            listaDao.eliminar(uid, idMultimedia, tipoLista)
            true
        } catch (e: Exception) { false }
    }

    fun obtenerIdsFavoritos(): kotlinx.coroutines.flow.Flow<List<Int>> {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return listaDao.obtenerIdsFavoritos(uid)
    }
    companion object {
        private const val BASE_URL = "http://192.168.1.19:8080/"
    }
    suspend fun agregarALista(solicitud: SolicitudLista): Boolean {
        return try {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Comprobamos si ya existe en Room
            val existe = listaDao.existe(uid, solicitud.idMultimedia, solicitud.tipoLista)

            if (existe) {
                listaDao.eliminar(uid, solicitud.idMultimedia, solicitud.tipoLista)
                Log.d("ROOM", "Eliminado de favoritos local")
            } else {
                listaDao.insertar(ListaEntity(
                    idMultimedia = solicitud.idMultimedia,
                    titulo = solicitud.titulo,
                    rutaPoster = solicitud.rutaPoster,
                    esPelicula = solicitud.esPelicula,
                    tipoLista = solicitud.tipoLista,
                    usuarioId = uid
                ))
                Log.d("ROOM", "Añadido a favoritos local")
            }
            true
        } catch (e: Exception) {
            Log.e("ROOM", "Error en Room: ${e.message}")
            false
        }
    }
}