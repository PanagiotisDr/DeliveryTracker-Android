package com.deliverytracker.app.domain.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PinHashResult(
    val hash: String,
    val salt: String
)

object PinHasher {
    private const val iterations = 120_000
    private const val keyLengthBits = 256
    private const val saltSizeBytes = 16
    private const val algorithm = "PBKDF2WithHmacSHA256"

    fun createHash(pin: String): PinHashResult {
        val salt = ByteArray(saltSizeBytes).also { SecureRandom().nextBytes(it) }
        val hashBytes = deriveKey(pin, salt)
        return PinHashResult(
            hash = Base64.getEncoder().encodeToString(hashBytes),
            salt = Base64.getEncoder().encodeToString(salt)
        )
    }

    fun verify(pin: String, saltBase64: String, expectedHashBase64: String): Boolean {
        val salt = Base64.getDecoder().decode(saltBase64)
        val expectedHash = Base64.getDecoder().decode(expectedHashBase64)
        val candidate = deriveKey(pin, salt)
        return MessageDigest.isEqual(candidate, expectedHash)
    }

    fun verifyLegacy(pin: String, expectedHashHex: String): Boolean {
        return hashLegacy(pin) == expectedHashHex
    }

    fun hashLegacy(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun deriveKey(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, keyLengthBits)
        val factory = SecretKeyFactory.getInstance(algorithm)
        return factory.generateSecret(spec).encoded
    }
}
