package org.dam2.appstreaming.data.remote.api

import org.dam2.appstreaming.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz para comunicarse con el servidor Spring Boot.
 */
interface ServicioApiBackend {

    // --- AUTENTICACION ---

    @POST("auth/registrar")
    suspend fun registrar(@Body solicitud: SolicitudRegistro): Response<RespuestaAutenticacion>

    @POST("auth/login")
    suspend fun login(@Body solicitud: SolicitudLogin): Response<RespuestaAutenticacion>

    // --- FAVORITOS ---

    @GET("api/favoritos/{nombreUsuario}")
    suspend fun obtenerFavoritos(@Path("nombreUsuario") nombreUsuario: String): Response<List<RespuestaFavorito>>

    @POST("api/favoritos/agregar")
    suspend fun agregarFavorito(@Body solicitud: SolicitudFavorito): Response<String>

    @DELETE("api/favoritos/eliminar/{nombreUsuario}/{idMultimedia}")
    suspend fun eliminarFavorito(
        @Path("nombreUsuario") nombreUsuario: String,
        @Path("idMultimedia") idMultimedia: Int
    ): Response<String>
}
