package com.github.enteraname74.cloudy.domain.auth

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom

class HashedPasswordManagerImpl : HashedPasswordManager {

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt: ByteArray = ByteArray(16)
        random.nextBytes(salt)

        return salt
    }

    override fun buildHashedPassword(password: String): HashedPassword? =
        try {
            val md = MessageDigest.getInstance(HASH_ALGORITHM)
            val salt: ByteArray = generateSalt()
            md.update(salt)

            val hashedPassword: ByteArray = md.digest(password.toByteArray(StandardCharsets.UTF_8))

            if (hashedPassword.isEmpty()) {
                null
            } else {
                HashedPassword(
                    hash = hashedPassword,
                    salt = salt
                )
            }
        } catch (_: Exception) {
            null
        }

    override fun isMatching(password: String, hashedPassword: HashedPassword): Boolean =
        try {
            val md = MessageDigest.getInstance(HASH_ALGORITHM)
            md.update(hashedPassword.salt)

            val hashToCompare = md.digest(password.toByteArray(StandardCharsets.UTF_8))

            hashToCompare.contentEquals(hashedPassword.hash)
        } catch (_: Exception) {
            false
        }

    companion object {
        private const val HASH_ALGORITHM: String = "SHA-512"
    }
}