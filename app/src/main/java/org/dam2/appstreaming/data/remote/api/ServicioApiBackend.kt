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

    // --- OBTENER LISTA ---

    @GET("api/listas/{nombreUsuario}/{tipoLista}")
    suspend fun obtenerContenidoLista(
        @Path("nombreUsuario") nombreUsuario: String,
        @Path("tipoLista") tipoLista: String // <--- Añadido el segundo parámetro
    ): Response<List<RespuestaLista>>
    @POST("api/listas/agregar")
    suspend fun agregarALista(@Body solicitud: SolicitudLista): Response<okhttp3.ResponseBody>

    // 3. Eliminar de una lista
    // IMPORTANTE: Los nombres en @Path deben coincidir con tu Controlador de Spring Boot
    @DELETE("api/listas/eliminar/{nombreUsuario}/{tipoLista}/{idMultimedia}")
    suspend fun eliminarDeLista(
        @Path("nombreUsuario") nombre: String,
        @Path("tipoLista") tipo: String,
        @Path("idMultimedia") id: Int
    ): Response<String>

    @POST("auth/sincronizar")
    suspend fun sincronizarUsuario(
        @Body solicitud: SolicitudRegistro
    ): Response<okhttp3.ResponseBody>
}
