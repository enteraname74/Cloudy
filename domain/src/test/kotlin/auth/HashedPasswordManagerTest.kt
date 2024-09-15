package auth

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManagerImpl
import kotlin.test.Test
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
            message = "A hashed password should have been created.")

        val isPasswordMatching: Boolean = hashedPasswordManager.isMatching(
            password = password,
            hashedPassword = hashedPassword,
        )

        assertTrue(
            actual = isPasswordMatching,
            message = "The password should catch the hashed one."
        )
    }
}