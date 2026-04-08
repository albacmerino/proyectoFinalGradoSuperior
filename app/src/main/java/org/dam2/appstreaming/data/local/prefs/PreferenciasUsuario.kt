package org.dam2.appstreaming.data.local.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferenciasUsuario(context: Context) {

    // 1. Creamos una "Llave Maestra" para cifrar los datos
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 2. Creamos el archivo de preferencias cifrado (más seguro que el normal)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sesion_preferencias",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda la elección del Checkbox: ¿Quiere el usuario que le recordemos?
     */
    fun guardarMantenerSesion(mantener: Boolean) {
        sharedPreferences.edit().putBoolean("mantener_sesion", mantener).apply()
    }

    /**
     * Recupera lo que el usuario eligió la última vez.
     */
    fun obtenerMantenerSesion(): Boolean {
        // Por defecto devolvemos true (es lo más cómodo para el usuario)
        return sharedPreferences.getBoolean("mantener_sesion", true)
    }
}