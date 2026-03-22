package org.dam2.appstreaming.backend.model

import jakarta.persistence.*

/**
 * Entidad que representa a un usuario en la base de datos.
 */
@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val nombreUsuario: String,

    @Column(nullable = false)
    val contrasena: String,

)
