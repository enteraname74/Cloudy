package com.github.enteraname74.cloudy.config.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.model.UserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun ApplicationContext.generateToken(
    user: User,
    expireDate: Date,
    type: TokenType,
): String {
    val environment = this.application.environment
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()

    val userType: UserType = if (user.isAdmin) {
        UserType.Admin
    } else {
        UserType.User
    }

    return JWT.create()
        .withIssuer(issuer)
        .withClaim(TOKEN_USERNAME_CLAIM_KEY, user.username)
        .withClaim(TOKEN_USER_ID_CLAIM_KEY, user.id.toString())
        .withClaim(TOKEN_ROLE_CLAIM_KEY, userType.value)
        .withClaim(TOKEN_TYPE_CLAIM_KEY, type.value)
        .withExpiresAt(expireDate)
        .sign(Algorithm.HMAC256(secret))
}

fun ApplicationContext.getUsernameFromToken(): String? {
    val principal = call.principal<JWTPrincipal>()
    return principal?.payload?.getClaim(TOKEN_USERNAME_CLAIM_KEY)?.asString()
}

fun ApplicationContext.getUserIdFromToken(): UUID? {
    val principal = call.principal<JWTPrincipal>()
    return principal?.payload?.getClaim(TOKEN_USER_ID_CLAIM_KEY)?.asString()?.let { UUID.fromString(it) }
}

fun ApplicationContext.isTokenARefreshOne(): Boolean {
    val principal = call.principal<JWTPrincipal>()
    return principal
        ?.payload
        ?.getClaim(TOKEN_TYPE_CLAIM_KEY)
        ?.asString()
        ?.let {
            TokenType.fromString(it)
        } == TokenType.Refresh
}

internal const val TOKEN_USERNAME_CLAIM_KEY = "username"
internal const val TOKEN_ROLE_CLAIM_KEY = "role"
internal const val TOKEN_USER_ID_CLAIM_KEY = "userId"
internal const val TOKEN_TYPE_CLAIM_KEY = "tokenType"

enum class TokenType(val value: String) {
    Access("Access"),
    Refresh("Refresh");

    companion object{
        fun fromString(token: String): TokenType? =
            entries.find { it.value == token }
    }
}
