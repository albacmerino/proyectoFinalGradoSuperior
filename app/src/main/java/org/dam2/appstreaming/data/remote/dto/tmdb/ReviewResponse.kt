package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * RESPUESTA DE RESEÑAS
 * 
 * Clase que mapea el listado de críticas de usuarios devuelto por la API de TMDB.
 *
 */
data class ReviewResponse(
    @SerializedName("results") val results: List<Review>
)

/**
 * Representa una crítica individual.
 */
data class Review(
    val author: String,
    val content: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails
)

/**
 * Detalles extendidos del autor de la reseña, incluyendo su avatar y puntuación personal.
 */
data class AuthorDetails(
    val name: String,
    val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    val rating: Double?
)
