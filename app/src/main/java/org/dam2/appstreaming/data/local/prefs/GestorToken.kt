package org.dam2.appstreaming.data.local.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * GESTOR DE TOKENS DE SEGURIDAD
 * 
 * Clase especializada en el almacenamiento seguro de identificadores de sesión (tokens).
 * Al igual que PreferenciasUsuario, implementa el cifrado de Jetpack Security para proteger
 * la integridad y confidencialidad de la información de autenticación.
 *
 * - Seguridad en Reposo: Implementa cifrado AES256 a nivel de hardware/software para mitigar riesgos de robo de identidad.
 * - JWT Ready: Preparado para almacenar tokens JWT de una futura integración con un backend RESTful.
 * - Singleton Context: Recibe el contexto de la aplicación para interactuar de forma segura con el sistema de archivos de Android.
 */
class GestorToken(context: Context) {
    // Generación de la llave maestra.
    private val llaveMaestra = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Contenedor de preferencias cifradas
    private val preferencias = EncryptedSharedPreferences.create(
        context,
        "preferencias_auth",
        llaveMaestra,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Almacena de forma cifrada el token del usuario.
     */
    fun guardarToken(token: String) {
        preferencias.edit {
            putString("token_jwt", token)
        }
    }
}
