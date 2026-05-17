package org.dam2.appstreaming.data.model

/**
 * INTERFAZ ITEM MULTIMEDIA
 *
 * Define el contrato común para todos los elementos de contenido de la aplicación (Películas y Series).
 */
interface ItemMultimedia {
    val id: Int
    val titulo: String
    val sinopsis: String
    val rutaPoster: String?
    val rutaFondo: String?
    val fechaLanzamiento: String?
    val puntuacionMedia: Double
    val idsGeneros: List<Int>?
    val enlaceWeb: String?
}
