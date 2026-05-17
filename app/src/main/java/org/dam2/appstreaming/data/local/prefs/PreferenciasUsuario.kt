package org.dam2.appstreaming.data.local.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * GESTOR DE PREFERENCIAS DEL USUARIO
 * 
 * Esta clase implementa el almacenamiento persistente de configuraciones ligeras y estados de sesión.
 * Utiliza EncryptedSharedPreferences para garantizar que los datos sensibles se almacenen de forma segura.
 *
 * - Seguridad de Datos: Uso de Jetpack Security para cifrar las preferencias mediante AES256.
 * - SharedPreferences: Aplicación del estándar de Android para la persistencia de pares clave-valor.
 * - Abstracción: Encapsula el acceso a las preferencias, permitiendo que el resto de la App 
 *   no tenga que manejar la lógica de cifrado ni las llaves maestras.
 */
class PreferenciasUsuario(context: Context) {

    // Creación de una "Llave Maestra" almacenada en el Android Keystore System
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Inicialización del archivo de preferencias cifrado
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sesion_preferencias",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Persiste la decisión del usuario sobre mantener la sesión activa.
     * @param mantener Booleano proveniente del Checkbox de la pantalla de Login.
     */
    fun guardarMantenerSesion(mantener: Boolean) {
        sharedPreferences.edit {
            putBoolean("mantener_sesion", mantener)
        }
    }

    /**
     * Recupera el estado de la sesión para determinar el flujo de inicio de la aplicación.
     */
    fun obtenerMantenerSesion(): Boolean {
        return sharedPreferences.getBoolean("mantener_sesion", false)
    }
}
