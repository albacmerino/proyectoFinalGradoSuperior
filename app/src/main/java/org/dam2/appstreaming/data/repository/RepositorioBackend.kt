package org.dam2.appstreaming.data.repository

import android.content.Context
import org.dam2.appstreaming.data.local.prefs.GestorToken
import org.dam2.appstreaming.data.remote.api.ServicioApiBackend
import org.dam2.appstreaming.data.remote.dto.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repositorio encargado de gestionar la comunicacion con el Backend Spring Boot.
 */
class RepositorioBackend(contexto: Context) {

    private val gestorToken = GestorToken(contexto)

    private val api = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // IP para emulador Android
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
                Result.failure(Exception("Error en el registro"))
            }
        } catch (e: Exception) {
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
            Result.failure(e)
        }
    }

    // --- FAVORITOS ---

    suspend fun obtenerFavoritos(nombreUsuario: String): List<RespuestaFavorito> {
        return try {
            val respuesta = api.obtenerFavoritos(nombreUsuario)
            if (respuesta.isSuccessful) {
                respuesta.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarFavorito(favorito: SolicitudFavorito): Boolean {
        return try {
            val respuesta = api.agregarFavorito(favorito)
            respuesta.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun cerrarSesion() {
        gestorToken.eliminarToken()
    }
}
