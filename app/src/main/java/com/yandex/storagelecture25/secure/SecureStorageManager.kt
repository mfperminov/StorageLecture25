package com.yandex.storagelecture25.secure

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yandex.storagelecture25.keystore.KeystoreManager

private const val KEY_TOKEN = "auth_token"

class SecureStorageManager(context: Context) {

    private val keystoreManager = KeystoreManager()
    private val masterKey = MasterKey.Builder(context, keystoreManager.keyAlias)
        .setKeyGenParameterSpec(keystoreManager.keyGenParameterSpec)
        .build()

    private val encryptedSharedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun saveToken(token: String) {
        encryptedSharedPrefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getToken(): String? {
        return encryptedSharedPrefs.getString(KEY_TOKEN, null)
    }

} 