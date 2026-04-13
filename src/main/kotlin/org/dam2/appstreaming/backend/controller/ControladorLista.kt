package org.dam2.appstreaming.backend.controller
import org.dam2.appstreaming.backend.dto.RespuestaLista
import org.dam2.appstreaming.backend.dto.SolicitudLista
import org.dam2.appstreaming.backend.service.ServicioLista
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controlador para gestionar los favoritos de los usuarios.
 */
@RestController
@RequestMapping("/api/listas")
class ControladorFavorito(private val servicioLista: ServicioLista) {

    //Obtiene la lista en base al nombre del usuario y al nombre de la lista
    @GetMapping("/{nombreUsuario}/{tipoLista}")
    fun obtenerLista(
        @PathVariable nombreUsuario: String
        ,@PathVariable tipoLista: String)
    : ResponseEntity<List<RespuestaLista>> {
        return try {
            val favoritos = servicioLista.obtenerContenidoDeLista(nombreUsuario,tipoLista)
            ResponseEntity.ok(favoritos)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/agregar")
    fun agregarALista(@RequestBody solicitud: SolicitudLista): ResponseEntity<String> {
        return try {
            servicioLista.agregarALista(solicitud)
            ResponseEntity.ok("Favorito agregado correctamente")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/eliminar/{nombreUsuario}/{tipoLista}/{idMultimedia}")
    fun eliminarDeLista(
        @PathVariable nombreUsuario: String,
        @PathVariable idMultimedia: Int,
        @PathVariable tipoLista: String
    ): ResponseEntity<String> {
        return try {
            servicioLista.eliminarDeLista(nombreUsuario, idMultimedia,tipoLista)
            ResponseEntity.ok("Contenido eliminado correctamente")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
