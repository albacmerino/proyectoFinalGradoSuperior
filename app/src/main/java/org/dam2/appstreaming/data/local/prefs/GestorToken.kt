package org.dam2.appstreaming.data.local.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Clase encargada de almacenar y recuperar el token JWT de forma segura.
 */
class GestorToken(context: Context) {
    private val llaveMaestra = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferencias = EncryptedSharedPreferences.create(
        context,
        "preferencias_auth",
        llaveMaestra,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun guardarToken(token: String) {
        preferencias.edit().putString("token_jwt", token).apply()
    }

    fun obtenerToken(): String? {
        return preferencias.getString("token_jwt", null)
    }

    fun eliminarToken() {
        preferencias.edit().remove("token_jwt").apply()
    }
}
