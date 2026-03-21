package org.dam2.appstreaming.data.model

/**
 * Interfaz común para Películas y Series.
 * Permite que los componentes de UI traten ambos tipos de forma genérica.
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
