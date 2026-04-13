package org.dam2.appstreaming.data.repository

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.dam2.appstreaming.data.local.prefs.GestorToken
import org.dam2.appstreaming.data.remote.api.ServicioApiBackend
import org.dam2.appstreaming.data.remote.dto.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repositorio encargado de gestionar la comunicacion con el Backend Spring Boot.
 */
class RepositorioBackend(contexto: Context) {

    private val gestorToken = GestorToken(contexto)

    // Configuramos un logger para ver las peticiones en el Logcat
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) // Aumentado a 30s
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServicioApiBackend::class.java)

    // --- AUTENTICACION ---

    suspend fun registrar(nombre: String, clave: String): Result<RespuestaAutenticacion> {
        return try {
            val respuesta = api.registrar(SolicitudRegistro(nombre, clave))
            if (respuesta.isSuccessful && respuesta.body() != null) {
                gestorToken.guardarToken(respuesta.body()!!.token)
                Result.success(respuesta.body()!!)
            } else {
                val errorMsg = respuesta.errorBody()?.string() ?: "Error desconocido"
                Log.e("RepositorioBackend", "Error en registro: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("RepositorioBackend", "Fallo de conexión", e)
            Result.failure(e)
        }
    }

    suspend fun login(nombre: String, clave: String): Result<RespuestaAutenticacion> {
        return try {
            val respuesta = api.login(SolicitudLogin(nombre, clave))
            if (respuesta.isSuccessful && respuesta.body() != null) {
                gestorToken.guardarToken(respuesta.body()!!.token)
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Log.e("RepositorioBackend", "Fallo de conexión en login", e)
            Result.failure(e)
        }
    }

    // --- FAVORITOS ---

    suspend fun obtenerContenidoLista(nombreUsuario: String, tipoLista: String): List<RespuestaLista> {
        return try {
            // Llamamos a la función corregida de la interfaz
            val respuesta = api.obtenerContenidoLista(nombreUsuario, tipoLista)
            if (respuesta.isSuccessful) {
                respuesta.body() ?: emptyList()
            } else {
                Log.e("RepositorioBackend", "Error obteniendo lista $tipoLista: ${respuesta.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RepositorioBackend", "Error de red obteniendo lista $tipoLista", e)
            emptyList()
        }
    }

    suspend fun agregarALista(solicitud: SolicitudLista): Boolean {
        return try {
            val respuesta = api.agregarALista(solicitud)

            if (respuesta.isSuccessful) {
                Log.d("RepositorioBackend", "Éxito al agregar a la lista ${solicitud.tipoLista}")
                true
            } else {
                Log.e("RepositorioBackend", "Error del servidor: ${respuesta.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("RepositorioBackend", "Error de conexión al agregar a lista", e)
            false
        }
    }
    /**
     * Elimina un contenido de una lista específica.
     */
    suspend fun eliminarDeLista(nombreUsuario: String, tipoLista: String, idMultimedia: Int): Boolean {
        return try {
            val respuesta = api.eliminarDeLista(nombreUsuario, tipoLista, idMultimedia)
            respuesta.isSuccessful
        } catch (e: Exception) {
            Log.e("RepositorioBackend", "Error eliminando de lista $tipoLista", e)
            false
        }
    }
    suspend fun sincronizarUsuario(uid: String, nombre: String, email: String?): Boolean {
        return try {
            // Usamos el DTO de registro que configuramos antes
            val solicitud = SolicitudRegistro(
                uid = uid,
                nombreUsuario = nombre,
                email = email
            )

            // Llamamos a la API (asegúrate de que el método esté en ServicioApiBackend)
            val respuesta = api.sincronizarUsuario(solicitud)

            respuesta.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("RepositorioBackend", "Error en sincronización: ${e.message}")
            false
        }
    }

    fun cerrarSesion() {
        gestorToken.eliminarToken()
    }

    companion object {
        // 10.0.2.2 es la dirección IP especial que apunta al 'localhost' de tu ordenador desde el emulador Android.
        // Si usas un dispositivo físico, debes cambiar esta IP por la IP local de tu PC (ej. 192.168.1.45).
        private const val BASE_URL = "http://192.168.1.19:8080/"
    }
}
