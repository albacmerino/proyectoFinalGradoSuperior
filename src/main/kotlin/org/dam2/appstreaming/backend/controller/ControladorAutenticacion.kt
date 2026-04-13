package org.dam2.appstreaming.backend.controller

import org.dam2.appstreaming.backend.dto.RespuestaAutenticacion
import org.dam2.appstreaming.backend.dto.SolicitudAutenticacion
import org.dam2.appstreaming.backend.dto.SolicitudRegistro
import org.dam2.appstreaming.backend.model.Usuario
import org.dam2.appstreaming.backend.service.ServicioAutenticacion
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controlador que expone los puntos de entrada para el registro y login.
 */
@RestController
@RequestMapping("/auth")
class ControladorAutenticacion(private val servicioAutenticacion: ServicioAutenticacion) {

    @PostMapping("/registrar")
    fun registrar(@RequestBody solicitud: SolicitudAutenticacion): ResponseEntity<Any> {
        return try {
            val respuesta = servicioAutenticacion.registrar(solicitud)
            ResponseEntity.ok(respuesta)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody solicitud: SolicitudAutenticacion): ResponseEntity<Any> {
        return try {
            val respuesta = servicioAutenticacion.login(solicitud)
            ResponseEntity.ok(respuesta)
        } catch (e: Exception) {
            ResponseEntity.status(401).body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/sincronizar") // <--- Esto completa la ruta /api/auth/sincronizar
    fun sincronizar(@RequestBody solicitud: SolicitudRegistro): ResponseEntity<String> {
        return try {
            // Lógica para guardar el usuario en PostgreSQL si no existe
            servicioAutenticacion.sincronizarUsuario(solicitud)
            ResponseEntity.ok("Usuario sincronizado")
        } catch (e: Exception) {
            ResponseEntity.status(500).body(e.message)
        }
    }

    @GetMapping("/usuarios")
    fun listarUsuarios(): ResponseEntity<List<Usuario>> {
        return ResponseEntity.ok(servicioAutenticacion.listarTodos())
    }
}
