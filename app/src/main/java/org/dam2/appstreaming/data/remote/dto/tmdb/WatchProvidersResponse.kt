package org.dam2.appstreaming.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

/**
 * RESPUESTA DE PROVEEDORES DE CONTENIDO
 * 
 * Clase que mapea la disponibilidad del contenido en diferentes plataformas de streaming (OTT).
 * Utiliza un Map para gestionar la localización por país (ej. "ES" para España).
 *
 */
data class WatchProvidersResponse(
    val results: Map<String, WatchCountryInfo>
)

/**
 * Información de disponibilidad específica por país.
 */
data class WatchCountryInfo(
    val link: String,                   // Enlace a la página de JustWatch
    val flatrate: List<Provider>? = null // Lista de plataformas de suscripción mensual
)

/**
 * Representa una plataforma de streaming individual.
 */
data class Provider(
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String
)
