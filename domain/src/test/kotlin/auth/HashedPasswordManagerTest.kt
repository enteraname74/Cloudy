package auth

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManagerImpl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HashedPasswordManagerTest {
    @Test
    fun givenPassword_whenHashing_thenShouldBeCheckable() {
        val hashedPasswordManager: HashedPasswordManager = HashedPasswordManagerImpl()
        val password = "MySuperDuperPassword"

        val hashedPassword: HashedPassword? = hashedPasswordManager.buildHashedPassword(
            password = password,
        )

        assertNotNull(
            actual = hashedPassword,
            message = "A hashed password should have been created."
        )

        val isPasswordMatching: Boolean = hashedPasswordManager.isMatching(
            password = password,
            hashedPassword = hashedPassword,
        )

        assertTrue(
            actual = isPasswordMatching,
            message = "The password should catch the hashed one."
        )
    }

    @Test
    fun givenPassword_whenHashing_thenShouldUseBcrypt() {
        val hashedPasswordManager: HashedPasswordManager = HashedPasswordManagerImpl()

        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = "MySuperDuperPassword",
        ) ?: error("A hashed password should have been created.")

        assertTrue(
            actual = hashedPassword.hash.startsWith("$2a$")
                || hashedPassword.hash.startsWith("$2b$")
                || hashedPassword.hash.startsWith("$2y$"),
            message = "The hashed password should use bcrypt."
        )
    }

    @Test
    fun givenWrongPassword_whenCheckingHash_thenShouldNotMatch() {
        val hashedPasswordManager: HashedPasswordManager = HashedPasswordManagerImpl()

        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = "MySuperDuperPassword",
        ) ?: error("A hashed password should have been created.")

        val isPasswordMatching: Boolean = hashedPasswordManager.isMatching(
            password = "NotThePassword",
            hashedPassword = hashedPassword,
        )

        assertFalse(
            actual = isPasswordMatching,
            message = "A wrong password should not match the hashed one."
        )
    }
}
