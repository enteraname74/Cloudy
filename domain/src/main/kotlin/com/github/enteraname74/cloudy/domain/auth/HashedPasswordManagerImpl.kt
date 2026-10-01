package com.github.enteraname74.cloudy.domain.auth

import org.mindrot.jbcrypt.BCrypt

class HashedPasswordManagerImpl : HashedPasswordManager {
    override fun buildHashedPassword(password: String): HashedPassword? =
        try {
            val hash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_LOG_ROUNDS))

            if (hash.isBlank()) {
                null
            } else {
                HashedPassword(hash = hash)
            }
        } catch (_: Exception) {
            null
        }

    override fun isMatching(password: String, hashedPassword: HashedPassword): Boolean =
        try {
            BCrypt.checkpw(
                password,
                hashedPassword.hash,
            )
        } catch (_: Exception) {
            false
        }

    companion object {
        private const val BCRYPT_LOG_ROUNDS: Int = 12
    }
}
