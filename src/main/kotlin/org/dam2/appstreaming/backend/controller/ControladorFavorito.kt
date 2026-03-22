package org.dam2.appstreaming.backend.controller

import org.dam2.appstreaming.backend.dto.RespuestaFavorito
import org.dam2.appstreaming.backend.dto.SolicitudFavorito
import org.dam2.appstreaming.backend.service.ServicioFavorito
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controlador para gestionar los favoritos de los usuarios.
 */
@RestController
@RequestMapping("/api/favoritos")
class ControladorFavorito(private val servicioFavorito: ServicioFavorito) {

    @GetMapping("/{nombreUsuario}")
    fun obtenerFavoritos(@PathVariable nombreUsuario: String): ResponseEntity<List<RespuestaFavorito>> {
        return try {
            val favoritos = servicioFavorito.obtenerFavoritos(nombreUsuario)
            ResponseEntity.ok(favoritos)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/agregar")
    fun agregarFavorito(@RequestBody solicitud: SolicitudFavorito): ResponseEntity<String> {
        return try {
            servicioFavorito.agregarFavorito(solicitud)
            ResponseEntity.ok("Favorito agregado correctamente")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/eliminar/{nombreUsuario}/{idMultimedia}")
    fun eliminarFavorito(
        @PathVariable nombreUsuario: String,
        @PathVariable idMultimedia: Int
    ): ResponseEntity<String> {
        return try {
            servicioFavorito.eliminarFavorito(nombreUsuario, idMultimedia)
            ResponseEntity.ok("Favorito eliminado correctamente")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
