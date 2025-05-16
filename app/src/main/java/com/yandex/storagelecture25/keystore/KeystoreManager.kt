package com.yandex.storagelecture25.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreManager {

    private val androidKeystoreType = "AndroidKeyStore"
    private val keyStore = KeyStore.getInstance(androidKeystoreType).apply {
        load(null)
    }
    val keyAlias = "default_key"
    private var ivSize = 0

    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        keyAlias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(BLOCK_MODE)
        .setEncryptionPaddings(ENCRYPTION_PADDING)
        .setIsStrongBoxBacked(true)
        .setKeySize(256)
        .build()

    init {
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            ALGORITHM,
            androidKeystoreType
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun getKey(): SecretKey {
        val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv

        ivSize = iv.size
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        val combined = ByteArray(iv.size + encryptedBytes.size)

        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)

        val iv = ByteArray(ivSize)
        System.arraycopy(combined, 0, iv, 0, iv.size)

        val encryptedBytes = ByteArray(combined.size - iv.size)
        System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        return String(cipher.doFinal(encryptedBytes))
    }

    private companion object {

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE

        // "AES/GCM/NoPadding"
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"
    }
} 