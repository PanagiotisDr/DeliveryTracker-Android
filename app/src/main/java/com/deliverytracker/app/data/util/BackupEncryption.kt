package com.deliverytracker.app.data.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Utility class για κρυπτογράφηση/αποκρυπτογράφηση backup αρχείων.
 * Χρησιμοποιεί AES-256-GCM με Android Keystore για ασφαλή αποθήκευση κλειδιού.
 */
object BackupEncryption {
    
    private const val KEYSTORE_ALIAS = "DeliveryTrackerBackupKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    
    /**
     * Κρυπτογραφεί ένα string (JSON backup).
     * Επιστρέφει Base64 encoded string με format: IV + Encrypted Data
     */
    fun encrypt(plainText: String): String {
        val key = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        
        // Συνδυασμός IV + encrypted data
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }
    
    /**
     * Αποκρυπτογραφεί ένα Base64 encoded encrypted string.
     */
    fun decrypt(encryptedData: String): String {
        val key = getOrCreateSecretKey()
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
        
        // Εξαγωγή IV και encrypted data
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encryptedBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
    
    /**
     * Ελέγχει αν ένα string είναι κρυπτογραφημένο (Base64 format).
     */
    fun isEncrypted(data: String): Boolean {
        return try {
            // Αν ξεκινάει με {, πιθανώς είναι plain JSON
            !data.trimStart().startsWith("{")
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Λαμβάνει ή δημιουργεί το secret key στο Android Keystore.
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        // Αν υπάρχει ήδη το κλειδί, το επιστρέφουμε
        val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }
        
        // Δημιουργία νέου κλειδιού
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            // Το κλειδί παραμένει ακόμα και μετά από factory reset
            setUserAuthenticationRequired(false)
        }.build()
        
        keyGenerator.init(keyGenSpec)
        return keyGenerator.generateKey()
    }
}
