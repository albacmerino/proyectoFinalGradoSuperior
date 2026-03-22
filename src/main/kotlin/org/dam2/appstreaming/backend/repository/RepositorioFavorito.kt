package org.dam2.appstreaming.backend.repository

import org.dam2.appstreaming.backend.model.Favorito
import org.dam2.appstreaming.backend.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para gestionar las peliculas y series favoritas en la base de datos.
 */
@Repository
interface RepositorioFavorito : JpaRepository<Favorito, Long> {
    // Busca todos los favoritos de un usuario concreto
    fun findByUsuario(usuario: Usuario): List<Favorito>
    
    // Comprueba si un usuario ya tiene una pelicula especifica en favoritos
    fun existsByUsuarioAndIdMultimedia(usuario: Usuario, idMultimedia: Int): Boolean
    
    // Permite borrar un favorito especifico de un usuario
    fun deleteByUsuarioAndIdMultimedia(usuario: Usuario, idMultimedia: Int)
}
