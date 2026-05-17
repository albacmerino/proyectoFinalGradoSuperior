package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * REPARTO DE ACTORES/ACTRICES
 * 
 * Clase que mapea el reparto y equipo técnico asociado a una obra audiovisual.
 *
 */
data class CreditsResponse(
    val cast: List<CastMember>
)

/**
 * Representa a un integrante del reparto principal.
 */
data class CastMember(
    val id: Int,                        // ID único del actor en TMDB
    val name: String,                  // Nombre real del actor
    val character: String,             // Nombre del personaje interpretado
    @SerializedName("profile_path") val profilePath: String? // Ruta a la imagen de perfil
)
